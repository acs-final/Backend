package acs.aws_final_project.domain.aitest;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {



    @Value("${gpt.api.url}")
    private String gpt_api_url;


    @Value("${gpt.model}")
    private String gpt_model;


    @Autowired
    private RestTemplate template;


    public Object summarize(String question){
        String requestMessage1 = "내 질문에 대한 응답을 json 형식으로 출력해줘.";
        String requestMessage2 = question;

        List<GptRequest.Messages> messages = new ArrayList<>();
        messages.add(new GptRequest.Messages("system",requestMessage1));
        messages.add(new GptRequest.Messages("user",requestMessage2));

        HashMap<String, String> response_format = new HashMap<>();
        response_format.put("type","json_object");

        GptRequest gptRequest = new GptRequest(gpt_model,messages,response_format);
        GptResponse gptResponse = template.postForObject(gpt_api_url, gptRequest, GptResponse.class);

        log.info("gptResponse: {} ", gptResponse);

        String content = gptResponse.getChoices().get(0).getMessage().getContent();

        log.info("content: {}", content);



        return content;
    }



}
