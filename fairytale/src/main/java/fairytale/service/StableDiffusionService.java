package fairytale.service;


import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.NovaHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fairytale.util.AmazonS3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StableDiffusionService {


    @Value("${aws.bedrock.stablediffusion.model}")
    private String STABLEDIFFUSION_MODEL_ID;


    //private final BedrockRuntimeClient bedrockRuntimeClient;

    private final AmazonS3UploadService amazonS3UploadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String SYSTEM_PROMPT_NOVA = "Create a cartoon-style illustration based on ";

    private final BedrockRuntimeClient bedrockRuntimeClient = bedrockRuntimeClientForStableDiffusion();


    public BedrockRuntimeClient bedrockRuntimeClientForStableDiffusion() {

        Region region = Region.US_WEST_2;

        log.info("Stable Diffusion Region: {}", region);

        return BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(240))
                        .apiCallAttemptTimeout(Duration.ofSeconds(240))
                        .build())
                .build();
    }


    public String createImage(String title, String fileName, String promptText) throws JsonProcessingException {
        String message = SYSTEM_PROMPT_NOVA + "\"" + promptText + "\"";
        String fileDirName = title + "/" + fileName + ".png";

        log.info("message: {}", message);

        Map<String, Object> payload = new HashMap<>();
        payload.put("mode", "text-to-image");

        payload.put("prompt", message);

        payload.put("aspect_ratio", "1:1");
        payload.put("output_format", "png");


        log.info("payload: {}", payload);

        String requestBody = objectMapper.writeValueAsString(payload);

        InvokeModelRequest request = InvokeModelRequest.builder()
                //.modelId("stability.sd3-5-large-v1:0")
                .modelId(STABLEDIFFUSION_MODEL_ID)
                //.modelId("stability.sd3-large-v1:0")
                .contentType("application/json")
                .body(SdkBytes.fromByteArray(requestBody.getBytes(StandardCharsets.UTF_8)))
                .build();

        // Bedrock API 호출
        InvokeModelResponse response = bedrockRuntimeClient.invokeModel(request);
        String responseBody = new String(response.body().asByteArray(), StandardCharsets.UTF_8);


        log.info("Response: {}", response);

        // 이미지 파일을 저장할 경로 지정
        //String filePath = "D:/novaImage/image5.png";  // 예시 경로, 저장할 실제 경로로 변경
        String filePath = "";

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

                ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Thumbnails.of(inputStream)
                        .size(800, 600)  // 크기 조정 (원하는 해상도로 변경 가능)
                        .outputQuality(0.7)  // 품질 조정 (0.1 ~ 1.0)
                        .outputFormat("png")
                        .toOutputStream(outputStream);

                filePath = uploadImage(outputStream.toByteArray(), fileDirName);

//                // 이미지 파일로 저장 (예: generated_image.png)
//                try (FileOutputStream fos = new FileOutputStream(filePath)) {
//                    fos.write(decodedBytes);
//                    log.info("Image saved as generated_image.png");
//                } catch (IOException e) {
//                    log.error("Error saving the image: ", e);
//                }

            }

            //return parseResponse(responseBody);
            return filePath;
            //return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Transactional
    public String uploadImage(byte[] imageBytes, String fileName) {
        String imageUrl;

        try {
            imageUrl = amazonS3UploadService.uploadImage(imageBytes, fileName,"stablediffusion-image");
        } catch (Exception e) {
            throw new NovaHandler(ErrorStatus.FILE_UPLOAD_FAILED);
        }

        return imageUrl;
    }
}
