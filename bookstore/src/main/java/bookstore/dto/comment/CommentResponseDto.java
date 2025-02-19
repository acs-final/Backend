package bookstore.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentResponseDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class CommentCreateDto{

        private Long commentId;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class CommentListDto{

        private Long commentId;
        private String username;
        private String content;
        private Float score;
        private LocalDateTime createdAt;
    }


}
