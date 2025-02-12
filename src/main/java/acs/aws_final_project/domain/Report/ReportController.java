package acs.aws_final_project.domain.Report;

import acs.aws_final_project.domain.Report.dto.ReportRequestDto;
import acs.aws_final_project.domain.Report.dto.ReportResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reports")
public class ReportController {

    private final ReportService reportService;

    // 독후감 생성 API
    @PostMapping("/")
    public ApiResponse<ReportResponseDto.ReportCreateDto> createReport(@RequestBody ReportRequestDto.ReportCreateDto createDto) {
        log.info("createReport API Request time: {}", LocalDateTime.now());
        ReportResponseDto.ReportCreateDto result = reportService.createReport(createDto);
        return ApiResponse.onSuccess(result);
    }

    // 단일 독후감 조회 API
    @GetMapping("/{reportId}")
    public ApiResponse<ReportResponseDto.ReportDetailDto> getReport(@PathVariable Long reportId) {
        log.info("getReport API Request time: {}", LocalDateTime.now());
        ReportResponseDto.ReportDetailDto result = reportService.getReport(reportId);
        return ApiResponse.onSuccess(result);
    }

    // 독후감 수정 API
    @PatchMapping("/{reportId}")
    public ApiResponse<ReportResponseDto.ReportCreateDto> updateReport(@PathVariable Long reportId,
                                                                       @RequestBody ReportRequestDto.ReportUpdateDto updateDto) {
        log.info("updateReport API Request time: {}", LocalDateTime.now());
        ReportResponseDto.ReportCreateDto result = reportService.updateReport(reportId, updateDto);
        return ApiResponse.onSuccess(result);
    }

    // 독후감 삭제 API
    @DeleteMapping("/{reportId}")
    public ApiResponse<ReportResponseDto.ReportCreateDto> deleteReport(@PathVariable Long reportId) {
        log.info("deleteReport API Request time: {}", LocalDateTime.now());
        Long id = reportService.deleteReport(reportId);
        ReportResponseDto.ReportCreateDto result = ReportResponseDto.ReportCreateDto.builder()
                .reportId(id)
                .build();
        return ApiResponse.onSuccess(result);
    }

    // 전체 독후감 목록 조회 API (제목과 평점만 반환)
    @GetMapping("/")
    public ApiResponse<List<ReportResponseDto.ReportListDto>> getReports() {
        log.info("getReports API Request time: {}", LocalDateTime.now());
        List<ReportResponseDto.ReportListDto> result = reportService.getReports();
        return ApiResponse.onSuccess(result);
    }
}
