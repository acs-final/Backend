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

//    @Autowired
//    private AmazonS3Client amazonS3Client;
//
//    @Value("${aws.s3.bucket.nova}")
//    private String bucketName;
//
//
//    public String uploadFile(MultipartFile file, String dirName) throws IOException {
//        String fileName = generateFileName(file, dirName);
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());
//
//        try (InputStream inputStream = file.getInputStream()) {
//            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
//            amazonS3Client.putObject(putObjectRequest);
//        } catch (IOException e) {
//            throw new IOException("Failed to upload file to S3", e);
//        }
//        return amazonS3Client.getUrl(bucketName, fileName).toString();
//    }
//
//    private String generateFileName(MultipartFile file, String dirName){
//        return dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
//    }
}