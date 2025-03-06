package fairytale.util;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.polly.PollyClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class AwsConfig {

    private final String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
    private final String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

    AtomicInteger counter = new AtomicInteger(0);
    List<Region> regions = List.of(Region.US_EAST_1, Region.US_WEST_2, Region.EU_WEST_1);

    public Region getNextRegion() {
        return regions.get(counter.getAndIncrement() % regions.size());
    }

    @Bean
    public BedrockRuntimeAsyncClient bedrockRuntimeAsyncClient(){


        return BedrockRuntimeAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();
    }

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClientForClaude() {

//        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
//                AwsBasicCredentials.create(accessKey, secretKey)
//        );
        //Region region = getNextRegion();

        //log.info("Region: {}", region);

        return BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(240))
                        .apiCallAttemptTimeout(Duration.ofSeconds(240))
                        .build())
                .build();
    }

    @Bean
    public BedrockRuntimeClient bedrockRuntimeClientForStableDiffusion() {

//        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
//                AwsBasicCredentials.create(accessKey, secretKey)
//        );
        Region region = Region.US_WEST_2;

        log.info("Region: {}", region);

        return BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallTimeout(Duration.ofSeconds(240))
                        .apiCallAttemptTimeout(Duration.ofSeconds(240))
                        .build())
                .build();
    }

    @Bean(name = "s3Client")
    public AmazonS3Client amazonS3Client() {


        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Bean
    public PollyClient pollyClient(){

        return PollyClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

    }
}