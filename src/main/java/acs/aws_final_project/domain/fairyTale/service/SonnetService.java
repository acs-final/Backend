package acs.aws_final_project.domain.fairyTale.service;

import acs.aws_final_project.domain.body.Body;
import acs.aws_final_project.domain.body.BodyRepository;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.fairyTale.FairyTaleConverter;
import acs.aws_final_project.domain.fairyTale.FairytaleRepository;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.domain.member.MemberRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.FairytaleHandler;
import acs.aws_final_project.global.response.exception.handler.MemberHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SonnetService {

    // sonnet 모델
    @Value("${aws.bedrock.sonnet.model}")
    private String SONNET_MODEL_ID;

    private final BedrockRuntimeClient bedrockRuntimeClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final FairytaleRepository fairyTaleRepository;
    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;

    private final FairyTaleService fairyTaleService;



    private final String SYSTEM_PROMPT_SONNET = "Create a fairy tale for [gender] kid using these elements: Genre: [Genre] and Challenge: [Challenge]. " +
            "Write in an enchanting, fairy tale style and a satisfying ending. Include vivid descriptions and a moral lesson.\n" +
            "Totally 10 pages of amount.\n" +
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
            "Do not use double quotes in any dialogues or direct quotes of pages";


    @Transactional
    public FairyTaleResponseDto.FairyTaleResultDto createFairyTale(String memberId, String genre, String gender, String challenge) {

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        String message = String.format(
                "genre = %s\n" +
                        "gender = %s\n" +
                        "challenge = $s",
                genre, gender, challenge
        );


        // AI에게 전할 message 생성
        Message requestMessage = Message.builder()
                .content(ContentBlock.fromText(message))
                .role(ConversationRole.USER)
                .build();

        // 프롬프트
        SystemContentBlock systemContentBlock = SystemContentBlock.builder()
                .text(SYSTEM_PROMPT_SONNET)
                .build();

        try {
            // AI 요청을 날린다.
            ConverseResponse response = bedrockRuntimeClient.converse(request -> request
                    .modelId(SONNET_MODEL_ID)
                    .messages(requestMessage)    //질문 text
                    .system(systemContentBlock)  //prompt 설정
                    .inferenceConfig(config -> config
                            .maxTokens(3600)    // 출력 토큰 최대 개수.
                            .temperature(1.0F)  // 랜덤성. 높을 수록 예측 불가. 0 ~ 1 사이 값.
                            .topP(0.9F)));      // 단어 선택 가능성. 1 -> 모든 단어 고려.


            // 응답값을 return
            log.info("Response: {}", response.output().message().content().get(0).text());

            //return response.output().message().content().get(0).text();
            String sonnetResponse = response.output().message().content().get(0).text();
            return getSonnetResult(findMember, sonnetResponse, genre);


        } catch (SdkClientException e) {
            log.error(e.toString(), e);
            throw new RuntimeException(e);
        }

    }



    public FairyTaleResponseDto.FairyTaleResultDto getSonnetResult(Member findMember, String sonnetResponse, String genre) {

        FairyTaleResponseDto.FairyTaleResultDto myresult = new FairyTaleResponseDto.FairyTaleResultDto();

        try {
            // JSON 파싱을 위한 ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();

            String fixedJson = preprocessJson(sonnetResponse);

            log.info("fixedJson: {}", fixedJson);

            JsonNode rootNode = objectMapper.readTree(fixedJson);

            log.info("RootNode: {}", rootNode);

            // Title 추출
            String title = rootNode.get("title").asText();

            // Body 추출 (page1, page2... 순서대로 정렬)
            JsonNode bodyNode = rootNode.get("body");

            log.info("BodyNode: {}", bodyNode);

            //List<FairyTaleResponseDto.Pages> pages = new ArrayList<>();
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
            //sortedBody.forEach((key, value) -> bodyText.append(value).append("\n"));

            StringBuilder resultPage = new StringBuilder();

            List<String> pages = new ArrayList<>(sortedBody.values());
            TreeMap<String, String> resultBody = new TreeMap<>();

            int j = 0;

            for (int i=0; i< pages.size(); i++){

                resultPage.append(pages.get(i));

                if (i%2==1){
                    j++;
                    String key = "page" + j;
                    resultBody.put(key, String.valueOf(resultPage));

                    resultPage = new StringBuilder("");

                }
            }

            log.info("mergedBody: {}", resultBody);


            // Prompt 추출
            JsonNode promptNode = rootNode.get("prompt");

            log.info("promptNode: {}", promptNode);
            TreeMap<String, String> sortedPrompt = new TreeMap<>();
//		promptNode.fields().forEachRemaining(entry -> sortedPrompt.put(entry.getKey(), entry.getValue().asText()));

            for (JsonNode item : promptNode) {
                item.fields().forEachRemaining(entry -> sortedPrompt.put(entry.getKey(), entry.getValue().asText()));
            }
            log.info("Prompt: {}", sortedPrompt);


            // 결과 출력
            System.out.println("Title: " + title);
            System.out.println("\nBody:\n" + bodyText);

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

//            resultBody.forEach((key, value) -> {
//                int i = 0;
//                String file = title + i;
//                mp3RequestDtos.add(FairyTaleConverter.toMp3RequestDto(title,file, value));
//            });

            log.info("Async image request: {}", imageRequestDtos);

            /* 동화 저장 시 평점 입력하는 부분 수정 필요 */

            Fairytale myFairytale = FairyTaleConverter.toFairyTale(findMember, title, 0F, 0F, 0, genre);


            fairyTaleRepository.save(myFairytale);

            List<FairyTaleResponseDto.StablediffusionResultDto> imageUrls = fairyTaleService.asyncImage(imageRequestDtos, myFairytale);
            List<FairyTaleResponseDto.PollyResultDto> mp3Urls = fairyTaleService.asyncPolly(mp3RequestDtos, myFairytale);


            log.info("image urls: {}", imageUrls);

            sortedBody.forEach((key, value) -> {
                String page = key.substring(4);
                Integer pageNumber = Integer.parseInt(page);
                log.info("page: {}", page);
                log.info("pageNumber: {}", pageNumber);

                Body body = FairyTaleConverter.toBody(value, pageNumber, myFairytale);
                bodyRepository.save(body);
            });

            return myresult.builder()
                    .fairytaleId(myFairytale.getFairytaleId())
                    .title(title)
                    .score(myFairytale.getAvgScore())
                    .genre(myFairytale.getGenre())
                    .body(resultBody)
                    .imageUrl(imageUrls)
                    .mp3Url(mp3Urls)
                    //.keywords(keywords)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new FairytaleHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

    }

    public static String preprocessJson(String rawJson) {
        return rawJson
                .replace("\\\"", "\"")  // 이스케이프 제거
                .replace("\"[", "[")    // 배열을 문자열이 아닌 JSON 배열로 변환
                .replace("]\"", "]")    // 배열 끝 처리
                .replace("\n", " ")   // 줄바꿈 처리
                .replace("\r", "")      // 캐리지 리턴 제거
                .replace("\t", " ");    // 탭을 공백으로 변환
    }















/*========================================================================================================================*/




    //public FairyTaleResponseDto.FairyTaleResultDto createFairyTaleByInvoke(String memberId, String genre, String gender, String challenge) {
    public Object createFairyTaleByInvoke(String memberId, String genre, String gender, String challenge) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

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
                        "Do not use double quotes in any dialogues or direct quotes of pages",

                gender,genre,  challenge
        );

        Map<String, Object> messages = new HashMap<>();
        messages.put("role", "user");

        Map<String, Object> content = new HashMap<>();
        content.put("type","text");
        content.put("text", text);

        messages.put("content", List.of(content));

        //log.info("messages: {}", messages);


        Map<String, Object> payload = new HashMap<>();
        payload.put("max_tokens", 3600);
        payload.put("messages", List.of(messages));
        payload.put("anthropic_version", "bedrock-2023-05-31");

        //log.info("payload: {}", payload);

//        String requestBody = String.format(
//                "{ \"anthropic_version\": \"bedrock-2023-05-31\", \"max_tokens\": 2048, " +
//                        "\"messages\": [ " +
//                        "  { \"role\": \"user\", \"content\": [{ \"type\": \"text\", \"text\": \"%s. %s\" }," +
//                        "  ] } " +  // ✅ 사용자 입력 설정
//                        //"  { \"role\": \"system\", \"content\": [{ \"type\": \"text\", \"text\": \"%s.\" }] }, " +  // ✅ 시스템 프롬프트 설정
//                        " ]}",
//                text, SYSTEM_PROMPT_SONNET
//        );

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


        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
        String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);

        log.info("Response: {}", response);

        return responseBody;

        //String sonnetResponse = responseBody;
        //return getSonnetResult(findMember, sonnetResponse, genre);

    }

//    public FairyTaleResponseDto.FairyTaleResultDto getSonnetResultJO(String sonnetResponse) throws ParseException {
//
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject = (JSONObject) jsonParser.parse(sonnetResponse);
//
//
//
//        log.info("JsonObject: {}", jsonObject);
//
//        JSONObject title = (JSONObject) jsonObject.get("title");
//        log.info("title: {}", title);
//        String titleResult = title.toString();
//
//        JSONObject body = (JSONObject) jsonObject.get("body");
//        JSONArray bodys = (JSONArray) jsonObject.get("body");
//        log.info("body: {}", body);
//        log.info("bodys: {}", bodys);
//        //ArrayList<String> bodies = body.
//
//        List<FairyTaleResponseDto.Pages> pages = new ArrayList<>();
//
//        for (int i=1; i<8; i++) {
//            String key = "page" + i;
//
//            String content = body.get("key").toString();
//            FairyTaleResponseDto.Pages page = FairyTaleResponseDto.Pages.builder().page(content).build();
//            pages.add(page);
//        }
//
//
//        JSONObject keywords = (JSONObject) jsonObject.get("keywords");
//        JSONArray keywordlist = (JSONArray) jsonObject.get("keywords");
//        log.info("keywords: {}", keywords);
//        log.info("keywordlist: {}", keywordlist);
//
//        for (int i=1; i<6; i++){
//
//        }
//
//
//        return FairyTaleResponseDto.FairyTaleResultDto.builder()
//                .title(titleResult)
//                //.body(pages)
//                .build();
//
//    }

}
