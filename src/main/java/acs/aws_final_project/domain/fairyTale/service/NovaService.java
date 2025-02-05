package acs.aws_final_project.domain.fairyTale.service;

import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.NovaHandler;
import acs.aws_final_project.global.util.AmazonS3UploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NovaService {


    @Value("${aws.bedrock.nova.model}")
    private String NOVA_MODEL_ID;


    private final BedrockRuntimeClient bedrockRuntimeClient;

    private final AmazonS3UploadService amazonS3UploadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String SYSTEM_PROMPT_NOVA = "Create a cartoon-style illustration based on ";

    public Object createImage(String promptText) throws JsonProcessingException {
        String message = promptText;

        log.info("message: {}", message);

        Map<String, Object> payload = new HashMap<>();
        payload.put("taskType", "TEXT_IMAGE");

        Map<String, Object> textToImageParams = new HashMap<>();
        textToImageParams.put("text", message);

        payload.put("textToImageParams", textToImageParams);

        Map<String, Object> imageConfig = new HashMap<>();
        imageConfig.put("numberOfImages", 1);
//        imageConfig.put("width", 1024);
//        imageConfig.put("height", 1024);
//        imageConfig.put("cfgScale", 7.5); // 이미지 품질 조정
//        imageConfig.put("seed", 42); // 랜덤성 고정

        payload.put("imageGenerationConfig", imageConfig);

        log.info("payload: {}", payload);

        String requestBody = objectMapper.writeValueAsString(payload);

        // "content"의 올바른 형식 적용
//        Map<String, String> contentObject = new HashMap<>();
//        contentObject.put("text", message);
//
//        Map<String, Object> userMessage = new HashMap<>();
//        userMessage.put("role", "user");
//        userMessage.put("content", List.of(contentObject));
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("messages", List.of(userMessage));
//
//        String requestBody = objectMapper.writeValueAsString(payload);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("amazon.nova-canvas-v1:0") // ✅ Nova Canvas 모델 사용
                .contentType("application/json")
                .body(SdkBytes.fromByteArray(requestBody.getBytes(StandardCharsets.UTF_8)))
                .build();

        // Bedrock API 호출
        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
        String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);


        log.info("Response: {}", response);
        //log.info("ResponseBody: {}", responseBody);

        // 이미지 파일을 저장할 경로 지정
        String filePath = "D:/novaImage/image3.png";  // 예시 경로, 저장할 실제 경로로 변경

        // 이미지를 파일로 저장하는 메소드 호출
        //saveImageFromBase64(responseBody, filePath);


        try {

            // 응답은 JSON 형식으로 Base64 인코딩된 이미지들이 담겨있을 수 있음
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            List<String> images = (List<String>) responseMap.get("images");

            if (images != null && !images.isEmpty()) {
                String base64Image = images.get(0); // 첫 번째 이미지를 추출

                // Base64 디코딩
                byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

                // 이미지 파일로 저장 (예: generated_image.png)
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(decodedBytes);
                    log.info("Image saved as generated_image.png");
                } catch (IOException e) {
                    log.error("Error saving the image: ", e);
                }

                //log.info("Decoded Data: {}", decodedBytes);
            }

            //return parseResponse(responseBody);
            return filePath;
            //return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String parseResponse(String responseBody) throws Exception {
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        return responseMap.get("image_url").toString(); // 이미지 URL 반환
    }



//    @Transactional
//    public String uploadImage(MultipartFile imageFile) {
//        String imageUrl;
//
//        try {
//            imageUrl = amazonS3UploadService.uploadFile(imageFile, "nova-image");
//        } catch (Exception e) {
//            throw new NovaHandler(ErrorStatus.FILE_UPLOAD_FAILED);
//        }
//
//        return imageUrl;
//    }
}
