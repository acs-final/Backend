package acs.aws_final_project.domain.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class BookstoreResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class BookstoreCreateDto{
        private Long bookstoreId;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class BookstoreResultDto{

        private String title;
        private String body;
        private Float score;
        private Long fairytaleId;
        private String imageUrl;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class BookstoreListResultDto{

        private Long bookstoreId;
        private String writer;
        private String title;
        private String genre;
        private Integer commentCount;
        private Float score;
        private LocalDate createdAt;
        private Long fairytaleId;

    }

}
