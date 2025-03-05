package fairytale.service;

import com.common.entity.Body;
import com.common.entity.Fairytale;
import com.common.entity.Member;
import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.FairytaleHandler;
import com.common.global.response.exception.handler.MemberHandler;
import com.common.repository.BodyRepository;
import com.common.repository.FairytaleRepository;
import com.common.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fairytale.FairyTaleConverter;
import fairytale.dto.fairyTale.FairyTaleRequestDto;
import fairytale.dto.fairyTale.FairyTaleResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StreamingService {

    // sonnet 모델
    @Value("${aws.bedrock.sonnet.model}")
    private String SONNET_MODEL_ID;

    private final BedrockRuntimeClient bedrockRuntimeClientForClaude;
    private final BedrockRuntimeAsyncClient bedrockRuntimeAsyncClient;

    private final FairytaleRepository fairyTaleRepository;
    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;

    private final FairyTaleService fairyTaleService;

    private static Integer SESSION_COUNT = 0;

    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);   // 최소 10개의 스레드 유지
        executor.setMaxPoolSize(50);    // 최대 50개까지 확장 가능
        executor.setQueueCapacity(100); // 100개의 작업까지 대기 가능
        executor.setThreadNamePrefix("AsyncThread-fairytale"); // 스레드 이름 지정
        executor.initialize();
        return executor;
    }

    /*
     * 스트리밍 응답 + 프롬프트 이미지 분리
     * */

    public Object createPromptText(String body) {
        String text = String.format(
                "Create a prompt text in detail for stable diffusion model per 2 sheets based on [%s].\n" +
                        "All images generated by stable diffusion model must be consistent style and main character in fairy tale has consistent appearance.\n" +
                        //"Make the result as json format like \n" +
                        "The response is always a **valid JSON object** like \n" +
                        "\"\n" +
                        "{\n" +
                        "\"prompt\":\" \"\\[\n" +
                        "{\"prompt\": \"...\"},\n" +
                        "{\"prompt\": \"...\"},\n" +
                        "\\]\" \\n\n" +
                        "}\n" +
                        "\", which each value of list should be in one line.\n" +
                        "All responses are under 3600 tokens." +
//                        "Do not use double quotes in any dialogues or direct quotes of pages." +
                        "Do not say any words except json response",

                body
        );

        // JSON 파싱을 위한 ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> messages = new HashMap<>();
        messages.put("role", "user");

        Map<String, Object> content = new HashMap<>();
        content.put("type", "text");
        content.put("text", text);

        messages.put("content", List.of(content));

        //log.info("messages: {}", messages);


        Map<String, Object> payload = new HashMap<>();
        payload.put("max_tokens", 3600);
        payload.put("messages", List.of(messages));
        payload.put("anthropic_version", "bedrock-2023-05-31");

        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(SONNET_MODEL_ID)
                .contentType("application/json")
                .body(SdkBytes.fromUtf8String(requestBody))
                //.body(SdkBytes.fromByteArray(requestBody.getBytes(StandardCharsets.UTF_8)))
                .build();


        InvokeModelResponse response = bedrockRuntimeClientForClaude.invokeModel(request);
        String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);

        JsonNode prompt;
        try {


            String fixedJson = preprocessJsonByInvoke(responseBody);

            log.info("fixedJson: {}", fixedJson);

            JsonNode rootNode = objectMapper.readTree(fixedJson);

            log.info("RootNode: {}", rootNode);


            JsonNode pcontent = rootNode.get("content");
            log.info("Jackson Content: {}", content);

            JsonNode usage = rootNode.get("usage");
            log.info("Jackson Content usage: {}", usage);

            JsonNode ptext = pcontent.get(0).get("text");
            log.info("text: {}", text);

            prompt = ptext.get("prompt");
            log.info("prompt: {}", prompt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FairytaleHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

        return prompt;
    }

    @Transactional
    public CompletableFuture<SseEmitter> createFtWithStreaming(String memberId, String genre, String gender, String challenge) throws InterruptedException {

        log.info("SESSION_COUNT: {}", ++SESSION_COUNT);

        log.info("::::::Thread Name : " + Thread.currentThread().getName());

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        ObjectMapper objectMapper = new ObjectMapper();

        SseEmitter emitter = new SseEmitter(60 * 1000L);

        String text = String.format(
                "Create a fairy tale for %s kid using these elements: Genre: %s and Challenge: %s. Write in an enchanting, classic fairy tale style and a satisfying ending. Include vivid descriptions and a moral lesson.\n" +
                        "Totally 12 pages of amount.\n" +
                        "Create a prompt text in detail for stable diffusion model per 2 pages.\n" +
                        "All images generated by stable diffusion model must be consistent style and main character in fairy tale has consistent appearance.\n" +
                        //"Make the result as json format like \n" +
                        "The response is always a **valid JSON object** like \n" +
                        "\"\n" +
                        "{\n" +
                        "\"title\":\"...\", \\n" +
                        "\"body\": \"\\[\n" +
                        "{\"page1\": \"...\"},\n" +
                        "{\"page2\": \"...\"},\n" +
                        "\\]\",\\n\n" +
                        "\"prompt\":\" \"\\[\n" +
                        "{\"prompt1\": \"...\"},\n" +
                        "{\"prompt2\": \"...\"},\n" +
                        "\\]\", \\n\n" +
                        "}\n" +
                        "\", which body is list type and each value of list should be in one line.\n" +
//            "The body texts have SSML tags for polly service." +
                        "All responses are under 3600 tokens." +
                        "All responses in Korean except prompt." +
                        "Do not use double quotes in any dialogues or direct quotes of pages." +
                        "Do not say any words except json response",

                gender, genre, challenge
        );

        /**
         *  claude sonnet v2 교차 리전
         */
        Map<String, Object> messages = new HashMap<>();
        messages.put("role", "user");

        Map<String, Object> content = new HashMap<>();
        content.put("type","text");
        content.put("text", text);



        messages.put("content", List.of(content));


        Map<String, Object> payload = new HashMap<>();
        payload.put("max_tokens", 3600);
        payload.put("messages", List.of(messages));
        payload.put("anthropic_version", "bedrock-2023-05-31");
        payload.put("top_k", 250);
//        payload.put("stop_sequences", "[\"\"]");
        payload.put("temperature", 1);
        payload.put("top_p", 0.999);


        String requestBody = null;
        try {
            requestBody = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }




        // Bedrock API 요청 생성
        InvokeModelWithResponseStreamRequest request = InvokeModelWithResponseStreamRequest.builder()
                .modelId(SONNET_MODEL_ID)
                .contentType("application/json")
                .accept("application/json")
                .body(SdkBytes.fromUtf8String(requestBody)) // 요청 본문
                .build();

        StringBuilder completeResponseTextBuffer = new StringBuilder();

        // 스트리밍 응답 핸들러
        InvokeModelWithResponseStreamResponseHandler responseHandler = InvokeModelWithResponseStreamResponseHandler.builder()
                .subscriber(InvokeModelWithResponseStreamResponseHandler.Visitor.builder()
                        .onChunk(chunk -> {
                            CompletableFuture.runAsync(() -> {

                                String result = chunk.bytes().asUtf8String();


                                JsonNode rootNode = null;
                                try {
                                    rootNode = objectMapper.readTree(result);


                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }


                                if (rootNode.has("type") && "content_block_delta".equals(rootNode.get("type").asText())) {
                                    JsonNode delta = rootNode.get("delta");
                                    log.info("delta: {}", delta);

                                    String streamingText = delta.get("text").toString().replaceAll("^\"|\"$", "");

                                    // Append the text to the response text buffer.
                                    completeResponseTextBuffer.append(streamingText);

                                    try {
                                        emitter.send(SseEmitter.event().name("message").data(streamingText));
                                    } catch (IOException e) {
                                        emitter.completeWithError(e);
                                    }

                                }


                            }, taskExecutor());

                        })
                        .build())
                .onComplete(() -> {
                    FairyTaleResponseDto.FairyTaleImageAndMp3Dto fairyTaleImageAndMp3Dto = new FairyTaleResponseDto.FairyTaleImageAndMp3Dto();
                    try {

                        Thread.sleep(1000);

                        fairyTaleImageAndMp3Dto = getStreamingResult(findMember, String.valueOf(completeResponseTextBuffer), genre);

                        //FairyTaleResponseDto.FairyTaleCreateDto createDto = new FairyTaleResponseDto.FairyTaleCreateDto(fairyTaleImageAndMp3Dto.getFairytaleId());

                        String result = "스트리밍 완료 " + fairyTaleImageAndMp3Dto.getFairytaleId();
                        log.info("스트리밍 완료 + fairytaleId: {}", result);
                        emitter.send(SseEmitter.event().name("complete").data(result));
                        //emitter.send(SseEmitter.event().name("FairytaleId").data(createDto));
                        Thread.sleep(2000);

                        log.info("Streaming Complete FairytaleId: {}", fairyTaleImageAndMp3Dto.getFairytaleId());

                        log.info("Total Streaming text: {}", completeResponseTextBuffer);
                        log.info("현재 실행 중인 Thread (onComplete 호출됨): {}", Thread.currentThread().getName());

                    } catch (IOException | InterruptedException e) {
                        log.info("스트리밍 중단 원인: {}", e.getMessage());
                        emitter.completeWithError(e);
                    }

                    emitter.complete();   // 스트리밍 종료.

                    /********************* 이미지, mp3 생성 *********************/
                    FairyTaleResponseDto.FairyTaleImageAndMp3Dto finalFairyTaleImageAndMp3Dto = fairyTaleImageAndMp3Dto;
                    CompletableFuture.runAsync(() -> {
                        try {
                            createImageAndMp3(finalFairyTaleImageAndMp3Dto.getSortedPrompt(), finalFairyTaleImageAndMp3Dto.getTitle(), finalFairyTaleImageAndMp3Dto.getResultBody(), finalFairyTaleImageAndMp3Dto.getMyFairytale());
                            log.info("Thread when creating image and mp3: {}", Thread.currentThread().getName());
                        } catch (JsonProcessingException e) {
                            log.error("Error creating image and mp3: {}", e.getMessage());
                        }
                    }, taskExecutor());


                })
                .onError(emitter::completeWithError)
                .build();



        int maxRetries = 5;
        int baseDelay = 100; // 초기 대기 시간 (밀리초)

        for (int i = 0; i < maxRetries; i++) {
            try {
                // Bedrock API 요청 실행
                bedrockRuntimeAsyncClient.invokeModelWithResponseStream(request, responseHandler);
                log.info("Bedrock Request Success in {} tries", i);
                break; // 성공하면 루프 종료
            } catch (ThrottlingException e) {
                int delay = baseDelay * (int) Math.pow(2, i);
                System.out.println("ThrottlingException 발생, " + delay + "ms 후 retrying...");
                Thread.sleep(delay);
            }
        }


        return CompletableFuture.completedFuture(emitter);
        //return emitter;
        //return completeResponseTextBuffer.toString();
    }



    public FairyTaleResponseDto.FairyTaleImageAndMp3Dto getStreamingResult(Member findMember, String streamingResult, String genre) {
        String fixedJson = preprocessJsonByInvoke(streamingResult);
        log.info("Total Streaming text: {}", fixedJson);

        ObjectMapper objectMapper = new ObjectMapper();

        String title = "";
        JsonNode bodyNode = null;
        JsonNode promptNode = null;
        try {
            JsonNode rootNode = objectMapper.readTree(fixedJson);

            log.info("RootNode: {}", rootNode);


            title = rootNode.get("title").asText();
            log.info("title: {}", title);

            bodyNode = rootNode.get("body");
            log.info("body: {}", bodyNode);

            promptNode = rootNode.get("prompt");
            log.info("prompt: {}", promptNode);
        } catch (JsonProcessingException e) {
            throw new FairytaleHandler(ErrorStatus.FAIRYTALE_PARSING_ERROR);
        }


        TreeMap<String, String> sortedBody = new TreeMap<>(Comparator.comparingInt(k -> Integer.parseInt(k.substring(4))));

        for (JsonNode p: bodyNode){
            p.fields().forEachRemaining(entry -> {
                        String output = Jsoup.parse(entry.getValue().asText()).text();
                        sortedBody.put(entry.getKey(), output);
                    }
            );
        }
        log.info("sortedBody: {}", sortedBody);

        // Body를 하나의 문자열로 합치기
        StringBuilder bodyText = new StringBuilder();

        StringBuilder resultPage = new StringBuilder();

        List<String> pages = new ArrayList<>(sortedBody.values());
        TreeMap<String, String> resultBody = new TreeMap<>();


        TreeMap<String, String> sortedPrompt = new TreeMap<>();

        for (JsonNode item : promptNode) {
            item.fields().forEachRemaining(entry -> sortedPrompt.put(entry.getKey(), entry.getValue().asText()));
        }
        log.info("sortedPrompt: {}", sortedPrompt);


        int j = 0;

        for (int i=0; i< pages.size(); i++){

            resultPage.append(pages.get(i));

            if (pages.size()%2==1 && i==pages.size()-1) {  // resultBody의 마지막 인덱스일 때
                if (sortedPrompt.size()%2==0){  // resultBody는 홀수 개수, prompt는 짝수 개수
                    //j++;
                    String key = "page" + j;
                    //resultBody.put(key, String.valueOf(resultPage.append(pages.get(i))));
                    String lastPaget = resultBody.get(key) + " " + pages.get(i);

                    resultBody.replace(key, lastPaget);
                } else {  // resultBody는 홀수 개수, prompt는 홀수 개수
                    j++;
                    String key = "page" + j;
                    resultBody.put(key, String.valueOf(resultPage));
                }

            } else if (i%2==1){
                j++;
                String key = "page" + j;
                resultBody.put(key, String.valueOf(resultPage));

                resultPage = new StringBuilder("");

            }


        }

        log.info("mergedBody: {}", resultBody);



        // 결과 출력
        System.out.println("Title: " + title);
        System.out.println("\nBody:\n" + bodyText);


        /************************ 디비에 저장 ************************/
        Fairytale myFairytale = FairyTaleConverter.toFairyTale(findMember, title, 0F, 0F, 0, genre, 0L);

        fairyTaleRepository.save(myFairytale);

        log.info("Saved FairytaleId: {}", myFairytale.getFairytaleId());

        //log.info("image urls: {}", imageUrls);

        sortedBody.forEach((key, value) -> {
            String page = key.substring(4);
            Integer pageNumber = Integer.parseInt(page);
            log.info("page: {}", page);
            log.info("pageNumber: {}", pageNumber);

            Body body = FairyTaleConverter.toBody(value, pageNumber, myFairytale);
            bodyRepository.save(body);
        });


        FairyTaleResponseDto.FairyTaleImageAndMp3Dto sonnetResultByInvoke = FairyTaleResponseDto.FairyTaleImageAndMp3Dto.builder()
                .fairytaleId(myFairytale.getFairytaleId())
                .myFairytale(myFairytale)
                .resultBody(resultBody)
                .sortedPrompt(sortedPrompt)
                .title(title)
                .build();



        return sonnetResultByInvoke;
    }


    /*
    * 이미지 생성 부분 따로 수행.*/

    public void createImageAndMp3(TreeMap<String, String> sortedPrompt, String title, TreeMap<String, String> resultBody, Fairytale myFairytale) throws JsonProcessingException {

        List<FairyTaleRequestDto.StablediffusionRequestDto> imageRequestDtos = new ArrayList<>();
        List<FairyTaleRequestDto.PollyRequestDto> mp3RequestDtos = new ArrayList<>();

        List<String> prompts = sortedPrompt.values().stream().toList();
        for (int i=1; i<=sortedPrompt.size(); i++){

            String file = title + "-" + i;
            log.info("imageFileName: {}", file);
            log.info("prompt{} : {}", i-1, prompts.get(i-1));
            imageRequestDtos.add(FairyTaleConverter.toImageRequestDto(title, file, prompts.get(i-1)));
        }

        List<String> mp3s = resultBody.values().stream().toList();
        for (int i=1; i<=resultBody.size(); i++){

            String file = title + "-" + i;
            log.info("mp3FileName: {}", file);
            log.info("mp3{}: {}", i-1,mp3s.get(i-1));
            mp3RequestDtos.add(FairyTaleConverter.toMp3RequestDto(title,file, mp3s.get(i-1)));
        }


        log.info("Async image request: {}", imageRequestDtos);

        fairyTaleService.asyncImage2(imageRequestDtos, myFairytale);
        fairyTaleService.asyncPolly2(mp3RequestDtos, myFairytale);

    }

    public static String preprocessJsonByInvoke(String rawJson) {
        return rawJson
                .replace("\\\"", "\"")  // 이스케이프 제거
                .replace("\"[", "[")    // 배열을 문자열이 아닌 JSON 배열로 변환
                .replace("]\"", "]")    // 배열 끝 처리
                .replace("\\n", " ")   // 줄바꿈 처리
                .replace("\r", "")      // 캐리지 리턴 제거
                .replace("\"{", "{")
                .replace("}\"", "}")
                .replace("\t", " ");    // 탭을 공백으로 변환
    }


}
