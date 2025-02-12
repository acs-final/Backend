package acs.aws_final_project.domain.comment;

import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.comment.dto.CommentRequestDto;
import acs.aws_final_project.domain.member.Member;

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
