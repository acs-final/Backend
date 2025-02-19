package bookstore;

import bookstore.dto.bookstore.BookstoreRequestDto;
import bookstore.dto.bookstore.BookstoreResponseDto;

import com.common.entity.Bookstore;
import com.common.entity.Comment;
import com.common.entity.Fairytale;
import com.common.entity.Member;
import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.BookstoreHandler;
import com.common.global.response.exception.handler.FairytaleHandler;
import com.common.global.response.exception.handler.MemberHandler;
import com.common.repository.BookstoreRepository;
import com.common.repository.CommentRepository;
import com.common.repository.FairytaleRepository;
import com.common.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookstoreService {

    private final BookstoreRepository bookstoreRepository;
    private final FairytaleRepository fairyTaleRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public BookstoreResponseDto.BookstoreResultDto getBookstore(Long bookstoreId){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(()-> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        List<Comment> findComment = commentRepository.findAllByBookstore(findBookstore);
        List<BookstoreResponseDto.BookstoreCommentsDto> commentsDtos = findComment.stream().map(CommentConverter::toBookstoreComment).toList();

        return BookstoreResponseDto.BookstoreResultDto.builder()
                .title(findBookstore.getTitle())
                .body(findBookstore.getBody())
                .score(findBookstore.getAvgScore())
                .fairytaleId(findBookstore.getFairytale().getFairytaleId())
                .imageUrl(findBookstore.getImageUrl())
                .comment(commentsDtos)
                .build();
    }

    @Transactional
    public BookstoreResponseDto.BookstoreCreateDto createBookstore(String memberId, BookstoreRequestDto.BookstoreCreateDto createDto){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        //Fairytale findFairytale = fairyTaleRepository.findByFairytaleId(createDto.getFairytaleId());
        Fairytale findFairytale = fairyTaleRepository.findById(createDto.getFairytaleId()).orElseThrow(()-> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        if (findFairytale == null){
            throw new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND);
        }

        Bookstore newBookstore = BookstoreConverter.toBookstore(findMember, createDto.getTitle(), createDto.getBody(), createDto.getScore(), createDto.getScore(), createDto.getScore(), 0, findFairytale, createDto.getImageUrl());

        Bookstore saveBookstore = bookstoreRepository.save(newBookstore);

        return BookstoreResponseDto.BookstoreCreateDto.builder()
                .bookstoreId(saveBookstore.getBookstoreId())
                .build();
    }

    @Transactional
    public BookstoreResponseDto.BookstoreCreateDto updateBookstore(String memberId, Long bookstoreId, BookstoreRequestDto.BookstoreUpdateDto updateDto){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(()->new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        if (findMember != findBookstore.getMember()){
            throw new MemberHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

        if (updateDto.getTitle() != null){
            findBookstore.setTitle(updateDto.getTitle());
        }
        if (updateDto.getBody() != null){
            findBookstore.setBody(updateDto.getBody());
        }
        if (updateDto.getScore() != null){
            int scoreCount = findBookstore.getCommentCount() + 1;
            float totalScore = findBookstore.getTotalScore() - findBookstore.getMyScore() + updateDto.getScore();

            Float avgScore = totalScore / scoreCount;

            findBookstore.setAvgScore(avgScore);
            findBookstore.setTotalScore(totalScore);

        }
        if (updateDto.getImageUrl() != null){
            findBookstore.setImageUrl(updateDto.getImageUrl());
        }

        Bookstore saveBookstore = bookstoreRepository.save(findBookstore);

        return BookstoreResponseDto.BookstoreCreateDto.builder()
                .bookstoreId(bookstoreId)
                .build();

    }

    @Transactional
    public Long deleteBookstore(String memberId, Long bookstoreId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        if (findMember != findBookstore.getMember()){
            throw new MemberHandler(ErrorStatus.FAIRYTALE_BAD_REQUEST);
        }

        bookstoreRepository.delete(findBookstore);

        return bookstoreId;
    }


    // 책방 목록 조회
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstores(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = new ArrayList<>();

        findBookstores.forEach(bs -> {
            resultDtos.add(new BookstoreResponseDto.BookstoreListResultDto(
                    bs.getBookstoreId(), bs.getMember().getName(),
                    bs.getTitle(), bs.getFairytale().getGenre(), bs.getCommentCount(),
                    bs.getAvgScore(), bs.getCreatedAt().toLocalDate(), bs.getFairytale().getFairytaleId()));
        });


        return resultDtos;
    }

    // 책방 목록 조회 - 최신순
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstoresByDate(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = findBookstores.stream()
                .sorted(Comparator.comparing(Bookstore::getCreatedAt).reversed())
                .map(bs -> new BookstoreResponseDto.BookstoreListResultDto(
                        bs.getBookstoreId(), bs.getMember().getName(),
                        bs.getTitle(), bs.getFairytale().getGenre(), bs.getCommentCount(),
                        bs.getAvgScore(), bs.getCreatedAt().toLocalDate(), bs.getFairytale().getFairytaleId()
                ))
                .collect(Collectors.toList());

        return resultDtos;
    }

    // 책방 목록 조회 - 댓글순
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstoresByComments(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        findBookstores = findBookstores.stream().sorted(Comparator.comparing(Bookstore::getCommentCount).reversed()).collect(Collectors.toList());

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = new ArrayList<>();

        findBookstores.forEach(bs -> {
            resultDtos.add(new BookstoreResponseDto.BookstoreListResultDto(
                    bs.getBookstoreId(), bs.getMember().getName(),
                    bs.getTitle(), bs.getFairytale().getGenre(), bs.getCommentCount(),
                    bs.getAvgScore(), bs.getCreatedAt().toLocalDate(), bs.getFairytale().getFairytaleId()
            ));
        });

        return resultDtos;
    }

}
