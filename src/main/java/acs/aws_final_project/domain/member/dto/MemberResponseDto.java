package acs.aws_final_project.domain.member.dto;

import acs.aws_final_project.domain.member.PersonalColor;
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
        private String color;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MemberResultDto{

        private String memberId;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MemberDetailDto{

        private String nickname;
        private String username;
        private Integer credit;
        private Integer childAge;
        private String color;


    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MyFairytaleDto{

        private Long fairytaleId;
        private String writer;
        private String title;
        private String genre;
        private boolean hasReport;
        private Float score;
        private LocalDate createdAt;
        private String imageUrl;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MyBookstoreDto{

        private Long bookstoreId;
        private String writer;
        private String title;
        private String genre;
        private Integer commentCount;
        private Float score;
        private LocalDate createdAt;
        private Long fairytaleId;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class MyReportDto{

        private Long reportId;
        private String writer;
        private String title;
        private String genre;
        private Float score;
        private LocalDate createdAt;
        private Long fairytaleId;

    }
}
