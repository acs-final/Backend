package acs.aws_final_project.domain.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BooksResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class RecommendedBook {
        private String title;
        private String author;
        //private String genre;
        private Integer price;
        private Float score;
        private String imageUrl;
        private String siteUrl;
    }

}
