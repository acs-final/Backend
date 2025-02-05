package acs.aws_final_project.domain;

import acs.aws_final_project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai")
public class AiController {

    private final GptService gptService;
    private final BedrockService bedrockService;
    private final DalleService dalleService;

    @PostMapping("/dalle")
    public ApiResponse<Object> imageGen(@RequestBody String question) throws Exception {

        Object result = dalleService.generateImage(question);

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/gpt")
    public ApiResponse<Object> gptSummarize(@RequestBody String question){

        Object content = gptService.summarize(question);


        return ApiResponse.onSuccess(content);
    }

    @PostMapping("/bedrock")
    public ApiResponse<Object> bedrock(@RequestBody String question){

        Object content = bedrockService.send(question);


        return ApiResponse.onSuccess(content);
    }
}
