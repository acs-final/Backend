package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.books.BooksGenre;
import acs.aws_final_project.domain.books.dto.BooksResponseDto;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.domain.fairyTale.service.FairyTaleService;
import acs.aws_final_project.domain.fairyTale.service.StableDiffusionService;
import acs.aws_final_project.domain.fairyTale.service.PollyService;
import acs.aws_final_project.domain.fairyTale.service.SonnetService;
import acs.aws_final_project.global.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.bedrockruntime.endpoints.internal.Value;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/fairytale")
public class FairyTaleController {

    private final FairyTaleService fairyTaleService;
    private final SonnetService sonnetService;
    private final StableDiffusionService stableDiffusionService;
    private final PollyService pollyService;


    // 동화책 목록 가져오기. pagenation 추가 해야함.
    @GetMapping("/")
    public ApiResponse<List<FairyTaleResponseDto.FairyTaleListDto>> getFairyTaleList(){

        log.info("getFairyTaleList API Request time: {}", LocalDateTime.now());

        List<FairyTaleResponseDto.FairyTaleListDto> findFairyTaleList = fairyTaleService.getFairyTaleList();

        return ApiResponse.onSuccess(findFairyTaleList);
    }


    @GetMapping("/{fairytaleId}")
    public ApiResponse<FairyTaleResponseDto.FairyTaleResultDto> getFairyTale(@PathVariable Long fairytaleId){

        log.info("getFairyTale API Request time: {}", LocalDateTime.now());

        FairyTaleResponseDto.FairyTaleResultDto findFairyTale = fairyTaleService.getFairyTale(fairytaleId);

        return ApiResponse.onSuccess(findFairyTale);
    }

    @PostMapping("/sonnet")
    public ApiResponse<FairyTaleResponseDto.FairyTaleResultDto> createFairyTale(@RequestHeader String memberId, @RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto){

        log.info("createFairyTale API Request time: {}", LocalDateTime.now());

        String genre = requestDto.getGenre();
        String gender = requestDto.getGender();
        String challenge = requestDto.getChallenge();

        FairyTaleResponseDto.FairyTaleResultDto result = sonnetService.createFairyTale(memberId, genre, gender, challenge);
        //String result = sonnetService.createFairyTale(genre, gender, challenge);

        //Object result = sonnetService.createFairyTaleByInvoke(genre, gender, challenge);

        return ApiResponse.onSuccess(result);
    }


    // 이미지 여러개 생성 요청
//    @PostMapping("/sdasync")
//    public ApiResponse<List<FairyTaleResponseDto.StablediffusionResultDto>> createImage(@RequestBody List<FairyTaleRequestDto.StablediffusionRequestDto> requestDto) throws JsonProcessingException {
//
//        log.info("API Request time: {}", LocalDateTime.now());
//
//
//        List<FairyTaleResponseDto.StablediffusionResultDto> result = fairyTaleService.asyncImage(requestDto);
//
//
//        return ApiResponse.onSuccess(result);
//    }

    // 이미지 하나만 생성 요청
    @PostMapping("/sd")
//    public ApiResponse<Object> createImage(@RequestParam("imageFile") MultipartFile imageFile){
    public ApiResponse<Object> createImageByAsync(@RequestBody FairyTaleRequestDto.StablediffusionRequestDto requestDto){

        log.info("API Request time: {}", LocalDateTime.now());

        Object result = null;
        try {
            result = stableDiffusionService.createImage(requestDto.getTitle(), requestDto.getFileName(), requestDto.getPrompt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ApiResponse.onSuccess(result);
    }

//    @PostMapping("/polly")
//    public ApiResponse<List<FairyTaleResponseDto.PollyResultDto>> createMP3(@RequestBody List<FairyTaleRequestDto.PollyRequestDto> requestDto){
//
//
//        //String mp3Dir = pollyService.createMP3(requestDto.getText(), fileDir, requestDto.getFileName());
//
//        List<FairyTaleResponseDto.PollyResultDto> mp3Dir = fairyTaleService.asyncPolly(requestDto);
//
//        //return ApiResponse.onSuccess(new FairyTaleResponseDto.PollyResultDto(mp3Dir));
//        return ApiResponse.onSuccess(mp3Dir);
//    }


    @GetMapping("/top")
    public ApiResponse<List<FairyTaleResponseDto.Top5>> getTop5(){

        log.info("getTop5 API Request time: {}", LocalDateTime.now());

        List<FairyTaleResponseDto.Top5> result = fairyTaleService.getTop5();

        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/{fairytaleId}/score")
    public ApiResponse<FairyTaleResponseDto.FairyTaleListDto> grantScore(@PathVariable Long fairytaleId, @RequestBody FairyTaleRequestDto.ScoreRequestDto requestDto){

        log.info("grantScore API Request time: {}", LocalDateTime.now());

        FairyTaleResponseDto.FairyTaleListDto result = fairyTaleService.grantScore(fairytaleId, requestDto.getScore());

        return ApiResponse.onSuccess(result);
    }

}
