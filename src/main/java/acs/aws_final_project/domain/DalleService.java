package acs.aws_final_project.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DalleService {

    @Value("${gpt.api.key}")
    private String apiKey;

    @Value("${gpt.dalle.url}")
    private String DALL_E_URL;


    private final String downloadPath = "D:/Intellij-workspace"; // 이미지를 저장할 로컬 경로


    public String generateImage(String message) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String prompt = "Create a cartoon-style illustration based on " + message;

            HttpPost request = new HttpPost(DALL_E_URL);

            // 설정: 헤더
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);

            // JSON Body 생성 (안전한 방식 사용)
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "dall-e-3");
            requestBody.put("prompt", prompt);
            requestBody.put("n", 1);
            requestBody.put("size", "1024x1024");


            String jsonBody = objectMapper.writeValueAsString(requestBody);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            // 요청 보내기
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    log.info("Reader: {}", reader);

                    String result = reader.lines().collect(Collectors.joining());

                    log.info("Result: {}", result);

                    String imageUrl = parsingUrl(result);
                    String created = parsingCreated(result);
                    log.info("ImageUrl: {}", imageUrl);
                    log.info("Created: {}", created);

                    downloadImage(imageUrl, created);

                    return result;   //result.data[0].url
                } else {
                    throw new RuntimeException("Failed to call OpenAI API: " + response.getCode());
                }
            }
        }
    }

    //로컬 경로에 다운로드.
    public void downloadImage(String imageUrl, String fileName) throws Exception {

        fileName = fileName + ".png";

        // URL 연결
        try (InputStream inputStream = new URL(imageUrl).openStream();
             FileOutputStream outputStream = new FileOutputStream(downloadPath + "/" + fileName)) {

            Path path = Paths.get(downloadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);  // 경로 생성
                System.out.println("경로가 없어 새로 생성했습니다: " + downloadPath);
            }

            // 이미지 파일 복사
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("이미지 다운로드 성공: " + downloadPath + "/" + fileName);
        } catch (Exception e) {
            throw new RuntimeException("이미지 다운로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public String parsingUrl(String result){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 최상위 JSON 파싱
            //JsonNode rootNode = objectMapper.readTree(result);
            //log.info("RootNode: {}", rootNode);
            //String resultString = rootNode.get("result").asText();

            // 중첩된 JSON 파싱
            JsonNode resultNode = objectMapper.readTree(result);
            log.info("RootNode: {}", resultNode);
            JsonNode dataNode = resultNode.get("data").get(0);
            log.info("DataNode: {}", dataNode);
            String url = dataNode.get("url").asText();


            System.out.println("Extracted URL: " + url);

            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String parsingCreated(String result){
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // 최상위 JSON 파싱
//            JsonNode rootNode = objectMapper.readTree(result);
//            log.info("RootNode: {}", rootNode);
//            String resultString = rootNode.get("result").asText();

            // 중첩된 JSON 파싱
            JsonNode resultNode = objectMapper.readTree(result);
            log.info("RootNode: {}", resultNode);
            JsonNode dataNode = resultNode.get("created");
            log.info("DataNode: {}", dataNode);
            String created = dataNode.asText();

            System.out.println("Extracted created: " + created);

            return created;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}