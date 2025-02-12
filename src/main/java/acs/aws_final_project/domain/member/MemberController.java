package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.member.dto.MemberRequestDto;
import acs.aws_final_project.domain.member.dto.MemberResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ApiResponse<MemberResponseDto.LoginResponseDto> login(@RequestHeader String memberId, @RequestHeader String name){

        log.info("login API Request time: {}", LocalDateTime.now());

        MemberResponseDto.LoginResponseDto result = memberService.login(memberId, name);

        return ApiResponse.onSuccess(result);


    }

    @GetMapping("/fairytale")
    public ApiResponse<List<MemberResponseDto.MyFairytaleDto>> getMyFairytale(@RequestHeader String memberId){

        log.info("getMyFairytale API Request time: {}", LocalDateTime.now());

        List<MemberResponseDto.MyFairytaleDto> result = memberService.getMyFairytale(memberId);

        return ApiResponse.onSuccess(result);

    }



}
