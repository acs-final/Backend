package acs.aws_final_project.domain.comment.dto;

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
        private String content;
        private Float score;
        private Long memberId;
        private LocalDateTime createdAt;
    }


}
