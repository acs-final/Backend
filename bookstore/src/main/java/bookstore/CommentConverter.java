package bookstore;


import bookstore.dto.bookstore.BookstoreResponseDto;
import bookstore.dto.comment.CommentRequestDto;
import com.common.entity.Bookstore;
import com.common.entity.Comment;
import com.common.entity.Member;


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
