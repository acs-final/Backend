package acs.aws_final_project.dto.report;

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
        private Long fairytaleId;  // 독후감 대상 스토리북 ID
        private String imageUrl;
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
