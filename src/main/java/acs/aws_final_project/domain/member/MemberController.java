package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.member.dto.MemberRequestDto;
import acs.aws_final_project.domain.member.dto.MemberResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "로그인 API",description = "회원 로그인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "토큰에서 나온 sub값을 memberId로 사용."),
    })
    public ApiResponse<MemberResponseDto.LoginResponseDto> login(@RequestHeader String memberId){

        log.info("login API Request time: {}", LocalDateTime.now());

        MemberResponseDto.LoginResponseDto result = memberService.login(memberId);

        return ApiResponse.onSuccess(result);

    }



    @PatchMapping("/")
    @Operation(summary = "회원 수정 API",description = "memberId 에 해당되는 회원 정보 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
            @Parameter(name = "requestDto", description = "닉네임, 유저 이름, 자녀 나이")
    })
    public  ApiResponse<MemberResponseDto.LoginResponseDto> updateMyProfile(@RequestHeader String memberId, @RequestBody MemberRequestDto.UpdateProfileDto requestDto){

        log.info("updateMyProfile API Request time: {}", LocalDateTime.now());

        MemberResponseDto.LoginResponseDto result = memberService.updateMyProfile(memberId, requestDto);

        return ApiResponse.onSuccess(result);

    }


    @DeleteMapping("/")
    @Operation(summary = "회원 탈퇴 API",description = "memberId 에 해당되는 멤버 회원탈퇴")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
    })
    public ApiResponse<MemberResponseDto.LoginResponseDto> deleteMember(@RequestHeader String memberId){

        log.info("deleteMember API Request time: {}", LocalDateTime.now());

        MemberResponseDto.LoginResponseDto result = memberService.deleteMember(memberId);

        return ApiResponse.onSuccess(result);

    }

    @GetMapping("/")
    @Operation(summary = "회원 상세 조회 API",description = "memberId 에 해당되는 회원 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
    })
    public ApiResponse<MemberResponseDto.MemberDetailDto> getMemberDetail(@RequestHeader String memberId){

        log.info("getMemberDetail API Request time: {}", LocalDateTime.now());

        MemberResponseDto.MemberDetailDto result = memberService.getMemberDetail(memberId);

        return ApiResponse.onSuccess(result);

    }

    @GetMapping("/fairytale")
    @Operation(summary = "내 동화책 조회 API",description = "memberId 에 해당되는 회원 동화책 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
    })
    public ApiResponse<List<MemberResponseDto.MyFairytaleDto>> getMyFairytale(@RequestHeader String memberId){

        log.info("getMyFairytale API Request time: {}", LocalDateTime.now());

        List<MemberResponseDto.MyFairytaleDto> result = memberService.getMyFairytale(memberId);

        return ApiResponse.onSuccess(result);

    }


    @GetMapping("/bookstore")
    @Operation(summary = "내 책방(자유게시판) 조회 API", description = "memberId 에 해당되는 회원 책방 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
    })
    public ApiResponse<List<MemberResponseDto.MyBookstoreDto>> getMyBookstore(@RequestHeader String memberId){

        log.info("getMyBookstore API Request time: {}", LocalDateTime.now());

        List<MemberResponseDto.MyBookstoreDto> result = memberService.getMyBookstore(memberId);

        return ApiResponse.onSuccess(result);

    }


    @GetMapping("/report")
    @Operation(summary = "내 독후감 조회 API",description = "memberId 에 해당되는 회원 독후감 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER404", description = "유저 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
    })
    public ApiResponse<List<MemberResponseDto.MyReportDto>> getMyReport(@RequestHeader String memberId){

        log.info("getMyReport API Request time: {}", LocalDateTime.now());

        List<MemberResponseDto.MyReportDto> result = memberService.getMyReport(memberId);

        return ApiResponse.onSuccess(result);

    }


}
