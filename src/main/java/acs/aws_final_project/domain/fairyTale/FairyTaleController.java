package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.domain.fairyTale.service.FairyTaleService;
import acs.aws_final_project.domain.fairyTale.service.NovaService;
import acs.aws_final_project.domain.fairyTale.service.SonnetService;
import acs.aws_final_project.global.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/fairytale")
public class FairyTaleController {

    private final FairyTaleService fairyTaleService;
    private final SonnetService sonnetService;
    private final NovaService novaService;

    @PostMapping("/sonnet")
    public ApiResponse<Object> createFairyTale(@RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto){

        String genre = requestDto.getGenre();
        String gender = requestDto.getGender();
        String challenge = requestDto.getChallenge();

        FairyTaleResponseDto.FairyTaleResultDto result = sonnetService.createFairyTale(genre, gender, challenge);
        //String result = sonnetService.createFairyTale(genre, gender, challenge);

        //Object result = sonnetService.createFairyTaleByInvoke(genre, gender, challenge);

        return ApiResponse.onSuccess(result);
    }


    @PostMapping("/nova")
    public ApiResponse<Object> createImage(@RequestBody String prompt){


        Object result = null;
        try {
            result = novaService.createImage(prompt);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return ApiResponse.onSuccess(result);
    }


}
