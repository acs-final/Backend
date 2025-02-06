package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.domain.fairyTale.service.FairyTaleService;
import acs.aws_final_project.domain.fairyTale.service.NovaService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/fairytale")
public class FairyTaleController {

    private final FairyTaleService fairyTaleService;
    private final SonnetService sonnetService;
    private final NovaService novaService;
    private final PollyService pollyService;


    @GetMapping("/{fairytaleId}")
    public ApiResponse<FairyTaleResponseDto.FairyTaleResultDto> getFairyTale(@PathVariable Long fairytaleId){

        log.info("getFairyTale API Request time: {}", LocalDateTime.now());

        FairyTaleResponseDto.FairyTaleResultDto findFairyTale = sonnetService.getFairyTale(fairytaleId);

        return ApiResponse.onSuccess(findFairyTale);
    }

    @PostMapping("/sonnet")
    public ApiResponse<Object> createFairyTale(@RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto){

        log.info("createFairyTale API Request time: {}", LocalDateTime.now());

        String genre = requestDto.getGenre();
        String gender = requestDto.getGender();
        String challenge = requestDto.getChallenge();

        FairyTaleResponseDto.FairyTaleResultDto result = sonnetService.createFairyTale(genre, gender, challenge);
        //String result = sonnetService.createFairyTale(genre, gender, challenge);

        //Object result = sonnetService.createFairyTaleByInvoke(genre, gender, challenge);

        return ApiResponse.onSuccess(result);
    }


    @PostMapping("/nova")
    public ApiResponse<Object> createImage(@RequestParam("imageFile") MultipartFile imageFile){

        log.info("API Request time: {}", LocalDateTime.now());

        Object result = null;
//        try {
//            result = novaService.createImage(prompt);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }

        //String result = novaService.uploadImage(imageFile);


        return ApiResponse.onSuccess(result);
    }


    @PostMapping("/polly")
    public ApiResponse<FairyTaleResponseDto.PollyResultDto> createMP3(@RequestBody FairyTaleRequestDto.PollyRequestDto requestDto){

        String fileDir = "D:/pollyMp3";

        String mp3Dir = pollyService.createMP3(requestDto.getText(), fileDir, requestDto.getFileName());


        return ApiResponse.onSuccess(new FairyTaleResponseDto.PollyResultDto(mp3Dir));
    }


}
