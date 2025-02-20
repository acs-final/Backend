package bookstore.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequestDto {


    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class CommentCreateDto{

        private String content;
        private Float score;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class CommentUpdateDto{

        private String content;
        private Float score;
    }

}
