package acs.aws_final_project.domain.Report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ReportCreateDto {
        private String title;
        private String body;
        private Float score;
        private Long memberId; // 독후감 작성자 ID
        private Long fairytaleId;  // 독후감 대상 스토리북 ID
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ReportUpdateDto {
        private String title;
        private String body;
        private Float score;
    }
}
