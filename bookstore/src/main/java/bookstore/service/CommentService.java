package bookstore.service;


import bookstore.CommentConverter;
import bookstore.dto.comment.CommentRequestDto;
import bookstore.dto.comment.CommentResponseDto;

import com.common.entity.Bookstore;
import com.common.entity.Comment;
import com.common.entity.Member;
import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.BookstoreHandler;
import com.common.global.response.exception.handler.CommentHandler;
import com.common.global.response.exception.handler.MemberHandler;
import com.common.repository.BookstoreRepository;
import com.common.repository.CommentRepository;
import com.common.repository.MemberRepository;
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

        int c = findBookstore.getCommentCount() + 1;

        findBookstore.setCommentCount(c);

        float totalScore = findBookstore.getTotalScore() + createDto.getScore();
        int scoreCount = findBookstore.getCommentCount() + 1;  // 평점 개수는 본인 꺼 포함 + 댓글 개수. 댓글 생성할때마다 1증가

        float avgScore = totalScore / scoreCount;

        findBookstore.setAvgScore(avgScore);
        findBookstore.setTotalScore(totalScore);

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
            float totalScore = findBookstore.getTotalScore() - findComment.getScore() + updateDto.getScore();
            int scoreCount = findBookstore.getCommentCount() + 1;  // 평점 개수는 본인 꺼 포함 + 댓글 개수. 댓글 수정이므로 개수 그대로.

            float avgScore = totalScore / scoreCount;

            findBookstore.setAvgScore(avgScore);
            findBookstore.setTotalScore(totalScore);

            bookstoreRepository.save(findBookstore);

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
            float totalScore = findBookstore.getTotalScore() - findComment.getScore();
            int scoreCount = findBookstore.getCommentCount();  // 평점 개수는 본인 꺼 포함 + 댓글 개수. 댓글 삭제이므로 개수 하나 빼줌.

            float avgScore = totalScore / scoreCount;

            findBookstore.setAvgScore(avgScore);
            findBookstore.setTotalScore(totalScore);

            bookstoreRepository.save(findBookstore);
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
