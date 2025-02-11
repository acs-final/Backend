package acs.aws_final_project.domain.comment;

import acs.aws_final_project.domain.bookstore.Bookstore;
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

}
