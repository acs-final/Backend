package acs.aws_final_project.global.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.time.Duration;


@Configuration
public class AwsConfig {

    private final String accessKey = System.getenv("aws_access_key_id");
    private final String secretKey = System.getenv("aws_secret_access_key");

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClient() {

//        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
//                AwsBasicCredentials.create(accessKey, secretKey)
//        );

        return BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(240))
                        .apiCallAttemptTimeout(Duration.ofSeconds(240))
                        .build())
                .build();
    }

//    @Bean(name = "s3Client")
//    public AmazonS3 amazonS3Client() {
//
//
//        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
//
//        return AmazonS3ClientBuilder
//                .standard()
//                .withRegion(Regions.AP_NORTHEAST_2)
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .build();
//    }
}