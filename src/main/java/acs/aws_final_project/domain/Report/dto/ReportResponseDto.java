package acs.aws_final_project.domain.Report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class ReportResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ReportCreateDto {
        private Long reportId;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ReportDetailDto {
        private Long reportId;
        private String title;
        private String body;
        private Float score;
        private String memberId;
        private Long fairytaleId;
        private LocalDateTime createdAt;
    }

    // 목록 DTO: 클라이언트에 독후감 제목과 평점만 전달
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ReportListDto {
        private String title;
        private Float score;
    }
}