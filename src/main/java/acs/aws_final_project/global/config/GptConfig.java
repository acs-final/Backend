package acs.aws_final_project.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GptConfig {

    @Value("${gpt.api.key}")
    private String gptKey;

    @Bean
    public RestTemplate template(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution)->{
            request.getHeaders().add("Authorization", "Bearer "+gptKey);
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}