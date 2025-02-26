package report;

import com.common.global.response.ApiResponse;
import org.springframework.aop.scope.ScopedProxyUtils;
import report.dto.ReportRequestDto;
import report.dto.ReportResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/test")
    public ApiResponse<String> testSonarqube() {
        log.info("testSonarqube API Request time: {}", LocalDateTime.now());
        System.out.println("first ci test1");
        System.out.println("first ci test2");
        System.out.println("first ci test3");
        System.out.println("first ci test4");
        System.out.println("first ci test5");
        System.out.println("first ci test6");
        System.out.println("first ci test7");
        System.out.println("first ci test8");
        System.out.println("first ci test9");
        System.out.println("first ci test10");
        return ApiResponse.onSuccess("Test Success!");
    }

    // 독후감 생성 API
    @PostMapping("/")
    @Operation(summary = "독후감 생성 API",description = "독후감 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT404", description = "독후감 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
            @Parameter(name = "createDto", description = "독후감 제목, 내용, 평점, 동화책 id")
    })
    public ApiResponse<ReportResponseDto.ReportCreateDto> createReport(@RequestHeader("memberId") String memberId, @RequestBody ReportRequestDto.ReportCreateDto createDto) {

        log.info("createReport API Request time: {}", LocalDateTime.now());

        ReportResponseDto.ReportCreateDto result = reportService.createReport(memberId, createDto);

        return ApiResponse.onSuccess(result);
    }

    // 단일 독후감 조회 API
    @GetMapping("/{reportId}")
    @Operation(summary = "독후감 상세 조회 API",description = "독후감 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT404", description = "독후감 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
            @Parameter(name = "reportId", description = "독후감 id")
    })
    public ApiResponse<ReportResponseDto.ReportDetailDto> getReport(@PathVariable("reportId") Long reportId) {
        log.info("getReport API Request time: {}", LocalDateTime.now());

        ReportResponseDto.ReportDetailDto result = reportService.getReport(reportId);

        return ApiResponse.onSuccess(result);
    }

    // 독후감 수정 API
    @PatchMapping("/{reportId}")
    @Operation(summary = "독후감 수정 API",description = "독후감 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT404", description = "독후감 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id"),
            @Parameter(name = "createDto", description = "독후감 제목, 내용, 평점")
    })
    public ApiResponse<ReportResponseDto.ReportCreateDto> updateReport(@RequestHeader("memberId") String memberId,
                                                                       @PathVariable("reportId") Long reportId,
                                                                       @RequestBody ReportRequestDto.ReportUpdateDto updateDto) {
        log.info("updateReport API Request time: {}", LocalDateTime.now());

        ReportResponseDto.ReportCreateDto result = reportService.updateReport(memberId, reportId, updateDto);

        return ApiResponse.onSuccess(result);
    }

    // 독후감 삭제 API
    @DeleteMapping("/{reportId}")
    @Operation(summary = "독후감 삭제 API",description = "독후감 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT404", description = "독후감 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "멤버 id")
    })
    public ApiResponse<ReportResponseDto.ReportCreateDto> deleteReport(@RequestHeader("memberId") String memberId, @PathVariable("reportId") Long reportId) {
        log.info("deleteReport API Request time: {}", LocalDateTime.now());

        Long id = reportService.deleteReport(memberId, reportId);

        ReportResponseDto.ReportCreateDto result = ReportResponseDto.ReportCreateDto.builder()
                .reportId(id)
                .build();

        return ApiResponse.onSuccess(result);
    }

    // 전체 독후감 목록 조회 API (제목과 평점만 반환)
    @GetMapping("/")
    @Operation(summary = "전체 독후감 목록 조회 API",description = "전체 독후감 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "REPORT404", description = "독후감 검색 타입이 유효하지 않습니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })

    public ApiResponse<List<ReportResponseDto.ReportListDto>> getReports() {
        log.info("getReports API Request time: {}", LocalDateTime.now());

        List<ReportResponseDto.ReportListDto> result = reportService.getReports();

        return ApiResponse.onSuccess(result);
    }
}
