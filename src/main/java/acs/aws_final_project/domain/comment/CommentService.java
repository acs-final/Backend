package acs.aws_final_project.domain.comment;

import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.bookstore.BookstoreRepository;
import acs.aws_final_project.domain.bookstore.dto.BookstoreRequestDto;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.comment.dto.CommentRequestDto;
import acs.aws_final_project.domain.comment.dto.CommentResponseDto;
import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.domain.member.MemberRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.BookstoreHandler;
import acs.aws_final_project.global.response.exception.handler.CommentHandler;
import acs.aws_final_project.global.response.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BookstoreRepository bookstoreRepository;

    @Transactional
    public CommentResponseDto.CommentCreateDto createComment(CommentRequestDto.CommentCreateDto createDto, String memberId, Long bookstoreId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        Integer c = findBookstore.getCommentCount();

        if (c == null){
            c = 0;
        }

        findBookstore.setCommentCount(++c);

        bookstoreRepository.save(findBookstore);

        Comment newComment = CommentConverter.toComment(createDto, findBookstore, findMember);

        Long newCommentId = commentRepository.save(newComment).getCommentId();

        return new CommentResponseDto.CommentCreateDto(newCommentId);

    }

    @Transactional
    public CommentResponseDto.CommentCreateDto updateComment(String memberId, Long bookstoreId, Long commentId, CommentRequestDto.CommentUpdateDto updateDto){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        if (findMember != findComment.getMember()){
            throw new MemberHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

        if (findBookstore != findComment.getBookstore()){
            throw new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND);
        }

        if (updateDto.getContent() != null){
            findComment.setContent(updateDto.getContent());
        }
        if (updateDto.getScore() != null){
            findComment.setScore(updateDto.getScore());
        }

        Comment saveComment = commentRepository.save(findComment);

        return CommentResponseDto.CommentCreateDto.builder()
                .commentId(commentId)
                .build();

    }

    @Transactional
    public Long deleteComment(String memberId, Long bookstoreId, Long commentId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Comment findComment = commentRepository.findById(commentId).orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        if (findMember != findComment.getMember()){
            throw new MemberHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

        Integer c = findBookstore.getCommentCount();

        if (c > 0){
            findBookstore.setCommentCount(--c);
        }

        bookstoreRepository.save(findBookstore);
        commentRepository.delete(findComment);

        return commentId;
    }

    public List<CommentResponseDto.CommentListDto> getCommentByDate(Long bookstoreId){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        List<Comment> findComment = commentRepository.findAllByBookstore(findBookstore);

        List<CommentResponseDto.CommentListDto> result = findComment.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(c -> new CommentResponseDto.CommentListDto(c.getCommentId(), c.getMember().getName(), c.getContent(), c.getScore(), c.getCreatedAt()))
                .toList();

        return result;
    }

    public List<CommentResponseDto.CommentListDto> getCommentByScore(Long bookstoreId){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        List<Comment> findComment = commentRepository.findAllByBookstore(findBookstore);

        List<CommentResponseDto.CommentListDto> result = findComment.stream()
                .sorted(Comparator.comparing(Comment::getScore).reversed())
                .map(c -> new CommentResponseDto.CommentListDto(c.getCommentId(), c.getMember().getName(), c.getContent(), c.getScore(),  c.getCreatedAt()))
                .toList();

        return result;
    }

}
