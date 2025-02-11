package acs.aws_final_project.domain.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookstoreRequestDto {


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class BookstoreCreateDto{

        private String title;
        private String body;
        private Float score;
        private String fairytaleTitle;
        private String imageUrl;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class BookstoreUpdateDto{

        private String title;
        private String body;
        private Float score;
        private String imageUrl;
    }

}
