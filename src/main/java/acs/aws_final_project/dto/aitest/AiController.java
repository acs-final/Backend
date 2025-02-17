package acs.aws_final_project.dto.aitest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/ai")
public class AiController {

//    private final SonnetService sonnetService;
//
////    private final GptService gptService;
////    private final BedrockService bedrockService;
////    private final DalleService dalleService;
////
////    @PostMapping("/dalle")
////    public ApiResponse<Object> imageGen(@RequestBody String question) throws Exception {
////
////        Object result = dalleService.generateImage(question);
////
////        return ApiResponse.onSuccess(result);
////    }
////
////    @PostMapping("/gpt")
////    public ApiResponse<Object> gptSummarize(@RequestBody String question){
////
////        Object content = gptService.summarize(question);
////
////
////        return ApiResponse.onSuccess(content);
////    }
////
//    @PostMapping("/bedrock")
//    public ApiResponse<Object> createFairyTaleByInvoke(
//            @RequestHeader("memberId") String memberId,
//            @RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto) {
//        log.info("createFairyTaleByInvoke API Request time: {}", LocalDateTime.now());
//
//        String genre = requestDto.getGenre();
//        String gender = requestDto.getGender();
//        String challenge = requestDto.getChallenge();
//
//        Object content = sonnetService.createFairyTaleByInvoke(memberId, genre, gender, challenge);
//
//
//        return ApiResponse.onSuccess(content);
//    }
}
