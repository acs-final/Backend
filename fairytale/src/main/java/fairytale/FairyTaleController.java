package fairytale;

import com.common.entity.BooksGenre;
import com.common.global.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import fairytale.dto.books.BooksResponseDto;
import fairytale.dto.fairyTale.FairyTaleRequestDto;
import fairytale.dto.fairyTale.FairyTaleResponseDto;
import fairytale.service.FairyTaleService;
import fairytale.service.StableDiffusionService;
import fairytale.service.PollyService;
import fairytale.service.SonnetService;

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
@RequestMapping("/v1/fairytale")
public class FairyTaleController {

    private final FairyTaleService fairyTaleService;
    private final SonnetService sonnetService;
    private final StableDiffusionService stableDiffusionService;
    private final PollyService pollyService;

    private final BooksService booksService;

    @GetMapping("/recommanded/")
    public ApiResponse<List<BooksResponseDto.RecommendedBook>> getRecommendedBooks(@RequestParam("genre") BooksGenre genre){

        log.info("getRecommendedBooks API Request time: {}", LocalDateTime.now());

        List<BooksResponseDto.RecommendedBook> result = booksService.getRecommendedBooks(genre);

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/")
    @Operation(summary = "전체 동화책 목록 조회 API", description = "회원 구분 없이 전체 동화책 목록 조회.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FAIRYTALE404", description = "", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<List<FairyTaleResponseDto.FairyTaleListDto>> getFairyTaleList() {
        log.info("getFairyTaleList API Request time: {}", LocalDateTime.now());
        List<FairyTaleResponseDto.FairyTaleListDto> findFairyTaleList = fairyTaleService.getFairyTaleList();
        return ApiResponse.onSuccess(findFairyTaleList);
    }

    @GetMapping("/{fairytaleId}")
    @Operation(summary = "동화책 상세 조회 API", description = "동화책 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FAIRYTALE404", description = "동화책을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "fairytaleId", description = "동화책 id")
    })
    public ApiResponse<FairyTaleResponseDto.FairyTaleResultDto> getFairyTale(@PathVariable("fairytaleId") Long fairytaleId) {
        log.info("getFairyTale API Request time: {}", LocalDateTime.now());
        FairyTaleResponseDto.FairyTaleResultDto findFairyTale = fairyTaleService.getFairyTale(fairytaleId);
        return ApiResponse.onSuccess(findFairyTale);
    }

    @PostMapping("/sonnet")
    @Operation(summary = "동화책 생성 API", description = "동화책 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER404", description = "회원을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
            @Parameter(name = "requestDto", description = "동화책 장르, 자녀 성별, 주제")
    })
    public ApiResponse<Object> createFairyTale(
            @RequestHeader("memberId") String memberId,
            @RequestBody FairyTaleRequestDto.FairyTaleCreateDto requestDto) {
        log.info("createFairyTale API Request time: {}", LocalDateTime.now());

        String genre = requestDto.getGenre();
        String gender = requestDto.getGender();
        String challenge = requestDto.getChallenge();

        //FairyTaleResponseDto.FairyTaleResultDto result = sonnetService.createFairyTale(memberId, genre, gender, challenge);
        Object result = sonnetService.createFairyTaleByInvoke(memberId, genre, gender, challenge);

        return ApiResponse.onSuccess(result);
    }


    @PostMapping("/{fairytaleId}/score")
    @Operation(summary = "동화책 평점 주기 API", description = "동화책 평점 주기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FAIRYTALE404", description = "동화책을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @Parameters({
            @Parameter(name = "fairytaleId", description = "동화책 id"),
            @Parameter(name = "requestDto", description = "평점")
    })
    public ApiResponse<FairyTaleResponseDto.FairyTaleListDto> grantScore(
            @PathVariable("fairytaleId") Long fairytaleId,
            @RequestBody FairyTaleRequestDto.ScoreRequestDto requestDto) {
        log.info("grantScore API Request time: {}", LocalDateTime.now());
        FairyTaleResponseDto.FairyTaleListDto result = fairyTaleService.grantScore(fairytaleId, requestDto.getScore());
        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/{fairytaleId}")
    @Operation(summary = "동화책 삭제 API", description = "동화책 소프트 딜리트 (삭제된 동화의 id를 반환)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    @Parameters({
            @Parameter(name = "fairytaleId", description = "동화책 id")
    })
    public ApiResponse<FairyTaleResponseDto.FairyTaleDeleteDto> deleteFairytale(@PathVariable("fairytaleId") Long fairytaleId) {
        log.info("deleteFairyTale API Request time: {}", LocalDateTime.now());
        try {
            fairyTaleService.deleteFairytale(fairytaleId);
        } catch (Exception e) {
            log.error("동화 삭제 중 오류 발생: {}", e.getMessage());
        }
        return ApiResponse.onSuccess(
                FairyTaleResponseDto.FairyTaleDeleteDto.builder()
                        .fairytaleId(fairytaleId)
                        .build()
        );
    }


    @GetMapping("/top")
    @Operation(summary = "동화책 TOP3 조회 API", description = "동화책 목록 중 평점순 3개 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FAIRYTALE404", description = "동화책을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<List<FairyTaleResponseDto.Top3>> getTop3() {
        log.info("getTop3 API Request time: {}", LocalDateTime.now());
        List<FairyTaleResponseDto.Top3> result = fairyTaleService.getTop3();
        return ApiResponse.onSuccess(result);
    }


    @GetMapping("/dashboard")
    @Operation(summary = "대시보드 조회 API", description = "대시보드 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FAIRYTALE404", description = "동화책을 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<FairyTaleResponseDto.Dashboard> getDashboard() {
        log.info("getDashboard API Request time: {}", LocalDateTime.now());
        FairyTaleResponseDto.Dashboard result = fairyTaleService.getDashboard();
        return ApiResponse.onSuccess(result);
    }

    @PostMapping("/sd")
    public ApiResponse<String> testStableDiffusion(@RequestBody String prompt) throws JsonProcessingException {
        log.info("getDashboard API Request time: {}", LocalDateTime.now());
        String result = stableDiffusionService.createImage("D:/novaImage", "image", prompt);
        return ApiResponse.onSuccess(result);
    }

}
