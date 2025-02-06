package acs.aws_final_project.domain.aitest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedrockService {

    @Value("${aws.bedrock.sonnet.model}")
    private String MODEL_ID;

    @Value("${aws.bedrock.sonnet.arn}")
    private String INFERENCE_PROFILE_ARN;

    private final String SYSTEM_PROMPT = "2줄 이상 분량으로 답변해줘";

    private final BedrockRuntimeClient bedrockRuntimeClient;

    public String send(String message) {
        // AI에게 전할 message 생성
        Message requestMessage = Message.builder()
                .content(ContentBlock.fromText(message))
                .role(ConversationRole.USER)
                .build();

        // 프롬프트
        SystemContentBlock systemContentBlock = SystemContentBlock.builder()
                .text(SYSTEM_PROMPT)
                .build();

        try {
            // AI 요청을 날린다.
            ConverseResponse response = bedrockRuntimeClient.converse(request -> request
                    .modelId(MODEL_ID)
                    .messages(requestMessage)
                    .system(systemContentBlock)
                    .inferenceConfig(config -> config
                            .maxTokens(1024)
                            .temperature(1.0F)
                            .topP(0.9F)));


            // 응답값을 return
            log.info("Response: {}", response.output().message().content().get(0).text());

            return response.output().message().content().get(0).text();
        } catch (SdkClientException e) {
            log.error(e.toString(), e);
            throw new RuntimeException(e);
        }

    }
}