package acs.aws_final_project.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemberResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class LoginResponseDto{

        private String memberId;
        private String name;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MyFairytaleDto{

        private String memberId;
        private Long fairytaleId;
        private String writer;
        private String title;
        private String genre;
        private boolean hasReport;
        private Float score;
        private LocalDate createdAt;
        private String imageUrl;

    }
}
