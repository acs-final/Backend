package acs.aws_final_project.converter;

import acs.aws_final_project.entity.Bookstore;
import acs.aws_final_project.dto.bookstore.BookstoreResponseDto;
import acs.aws_final_project.dto.comment.CommentRequestDto;
import acs.aws_final_project.entity.Comment;
import acs.aws_final_project.entity.Member;

public class CommentConverter {

    public static Comment toComment(CommentRequestDto.CommentCreateDto createDto, Bookstore bookstore, Member member){



        return Comment.builder()
                .bookstore(bookstore)
                .score(createDto.getScore())
                .member(member)
                .content(createDto.getContent())
                .build();

    }

    public static BookstoreResponseDto.BookstoreCommentsDto toBookstoreComment(Comment comment){

        return BookstoreResponseDto.BookstoreCommentsDto.builder()
                .commentId(comment.getCommentId())
                .username(comment.getMember().getName())
                .content(comment.getContent())
                .score(comment.getScore())
                .createdAt(comment.getCreatedAt())
                .build();
    }

}
