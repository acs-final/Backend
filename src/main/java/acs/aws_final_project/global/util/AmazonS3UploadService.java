package acs.aws_final_project.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


@Component
public class AmazonS3UploadService {

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${aws.s3.bucket.nova}")
    private String bucketName;

    public String uploadMP3(InputStream audioStream, String fileName, String fileDir) throws IOException {
        String fileNameResult = generateFileName(fileName, fileDir);

        try {
            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("audio/mpeg");  // MP3 파일로 인식되도록 설정

            // S3 업로드 요청
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileNameResult, audioStream, metadata);
            amazonS3Client.putObject(putObjectRequest);

            // 업로드된 파일의 URL 반환
            return amazonS3Client.getUrl(bucketName, fileNameResult).toString();
        } catch (Exception e) {
            throw new IOException("S3 업로드 중 오류 발생", e);
        }

    }


    public String uploadFile(MultipartFile file, String fileName, String dirName) throws IOException {
        String fileNameResult = generateFileName(fileName, dirName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileNameResult, inputStream, metadata);
            amazonS3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new IOException("Failed to upload file to S3", e);
        }
        return amazonS3Client.getUrl(bucketName, fileNameResult).toString();
    }

    private String generateFileName(String fileName, String dirName){

        return dirName + "/" + fileName;

        //return dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
}