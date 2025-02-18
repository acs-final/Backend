package fairytale.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class AmazonS3UploadService {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public String uploadMP3(InputStream audioStream, String fileName, String fileDir) throws IOException {
        String fileNameResult = generateFileName(fileName, fileDir);

        try {
            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            Instant uploadTime = Instant.now();

            metadata.setContentType("audio/mpeg");  // MP3 파일로 인식되도록 설정
            metadata.setLastModified(Date.from(uploadTime));


            // S3 업로드 요청
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileNameResult, audioStream, metadata);
            amazonS3Client.putObject(putObjectRequest);

            // 업로드된 파일의 URL 반환
            return amazonS3Client.getUrl(bucketName, fileNameResult).toString();
        } catch (Exception e) {
            throw new IOException("S3 업로드 중 오류 발생", e);
        }

    }


    public String uploadImage(byte[] imageBytes, String fileName, String dirName) {
        String fileNameResult = generateFileName(fileName, dirName);

        log.info("fileNameResult: {}", fileNameResult);

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ObjectMetadata metadata = new ObjectMetadata();
            Instant uploadTime = Instant.now();
            //metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/png");
            metadata.setLastModified(Date.from(uploadTime));

            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileNameResult, inputStream, metadata));

            return amazonS3Client.getUrl(bucketName, fileNameResult).toString();  // 🚀 업로드된 S3 URL 반환
        } catch (Exception e) {
            log.error("Error uploading image to S3", e);
            throw new RuntimeException("Failed to upload image to S3", e);
        }
    }

    private String generateFileName(String fileName, String dirName){

        return dirName + "/" + fileName;

        //return dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
}