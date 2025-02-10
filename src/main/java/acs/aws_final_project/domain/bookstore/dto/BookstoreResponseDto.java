package acs.aws_final_project.domain.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    }

}
