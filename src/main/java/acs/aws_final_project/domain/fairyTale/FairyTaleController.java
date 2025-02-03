package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
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

    @PostMapping()
    public ApiResponse<FairyTaleResponseDto.FairyTaleResultDto> createFairyTale(@RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto){

        String genre = requestDto.getGenre();
        String gender = requestDto.getGender();
        String challenge = requestDto.getChallenge();

        FairyTaleResponseDto.FairyTaleResultDto result = fairyTaleService.send(genre, gender, challenge);

        return ApiResponse.onSuccess(result);
    }


//    @PostMapping()
//    public ApiResponse<> createImage(@RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto){
//
//        String genre = requestDto.getGenre();
//        String gender = requestDto.getGender();
//        String challenge = requestDto.getChallenge();
//
//        FairyTaleResponseDto.FairyTaleResultDto result = fairyTaleService.send(genre, gender, challenge);
//
//
//    }


}
