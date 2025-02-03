package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FairyTaleService {

    // sonnet 모델
    @Value("${aws.bedrock.sonnet.model}")
    private String MODEL_ID;

    @Value("${aws.bedrock.sonnet.arn}")
    private String INFERENCE_PROFILE_ARN;

    private final BedrockRuntimeClient bedrockRuntimeClient;

    private final String SYSTEM_PROMPT_SONNET = "Create a fairy tale for [gender] kid using these elements: Genre: [Genre] and Challenge: [Challenge]. Write in an enchanting, classic fairy tale style and a satisfying ending. Include vivid descriptions and a moral lesson.\n" +
            "Totally 15 pages of amount.\n" +
            "Describe 5 keywords about the story at the end.\n" +
            "Make the result as json format like \n" +
            "\"\n" +
            "{\n" +
            "\"title\":\"...\", \\n\n" +
            "\"body\": \"\\[\n" +
            "\"page1\": \"...\",\n" +
            "\"page2\": \"...\",\n" +
            "\\]\",\\n\n" +
            "\"keywords\": \"\\[\n" +
            "\"...\"\n" +
            "\\]\"\n" +
            "}\n" +
            "\", which body and keywords are list type.\n" +
            "All responses in Korean.";

    //private final String SYSTEM_PROMPT_NOVA =


    public FairyTaleResponseDto.FairyTaleResultDto send(String genre, String gender, String challenge) {
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
                    .modelId(MODEL_ID)
                    .messages(requestMessage)
                    .system(systemContentBlock)
                    .inferenceConfig(config -> config
                            .maxTokens(2048)    // 출력 토큰 최대 개수.
                            .temperature(1.0F)  // 랜덤성. 높을 수록 예측 불가. 0 ~ 1 사이 값.
                            .topP(0.9F)));      // 단어 선택 가능성. 1 -> 모든 단어 고려.


            // 응답값을 return
            log.info("Response: {}", response.output().message().content().get(0).text());

            //return response.output().message().content().get(0).text();
            return getSonnetResult(response.output().message().content().get(0).text());


        } catch (SdkClientException e) {
            log.error(e.toString(), e);
            throw new RuntimeException(e);
        }

    }


    public FairyTaleResponseDto.FairyTaleResultDto getSonnetResult(String sonnetResponse) {

        FairyTaleResponseDto.FairyTaleResultDto myresult = new FairyTaleResponseDto.FairyTaleResultDto();

        try {
            // JSON 파싱을 위한 ObjectMapper 생성
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(sonnetResponse);

            // Title 추출
            String title = rootNode.get("title").asText();

            // Body 추출 (page1, page2... 순서대로 정렬)
            JsonNode bodyNode = rootNode.get("body");

            //List<FairyTaleResponseDto.Pages> pages = new ArrayList<>();
            TreeMap<String, String> sortedBody = new TreeMap<>();
            bodyNode.fields().forEachRemaining(entry -> sortedBody.put(entry.getKey(), entry.getValue().asText()));

            // Body를 하나의 문자열로 합치기
            StringBuilder bodyText = new StringBuilder();
            sortedBody.forEach((key, value) -> bodyText.append(value).append("\n"));

            // Keywords 추출
            JsonNode keywordsNode = rootNode.get("keywords");
            List<String> keywords = new ArrayList<>();
            keywordsNode.forEach(keyword -> keywords.add(keyword.asText()));

            // 결과 출력
            System.out.println("Title: " + title);
            System.out.println("\nBody:\n" + bodyText);
            System.out.println("\nKeywords: " + keywords);

            return myresult.builder()
                    .title(title)
                    .body(sortedBody)
                    .keywords(keywords)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return myresult;
    }

}
