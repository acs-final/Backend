package bookstore.dto.bookstore;

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
        private Long fairytaleId;
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
