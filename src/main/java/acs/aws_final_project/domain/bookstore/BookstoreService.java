package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.bookstore.dto.BookstoreRequestDto;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.comment.CommentRepository;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.BookstoreHandler;
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
    private final FairyTaleRepository fairyTaleRepository;
    private final CommentRepository commentRepository;

    public BookstoreResponseDto.BookstoreResultDto getBookstore(Long bookstoreId){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(()-> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        return BookstoreResponseDto.BookstoreResultDto.builder()
                .title(findBookstore.getTitle())
                .body(findBookstore.getBody())
                .score(findBookstore.getScore())
                .fairytaleId(findBookstore.getBookstoreId())
                .build();
    }

    @Transactional
    public BookstoreResponseDto.BookstoreCreateDto createBookstore(BookstoreRequestDto.BookstoreCreateDto createDto){

        Fairytale findFairytale = fairyTaleRepository.findByTitle(createDto.getFairytaleTitle());

        Bookstore newBookstore = BookstoreConverter.toBookstore(createDto.getTitle(), createDto.getBody(), createDto.getScore(), findFairytale, createDto.getImageUrl());

        Bookstore saveBookstore = bookstoreRepository.save(newBookstore);

        return BookstoreResponseDto.BookstoreCreateDto.builder()
                .bookstoreId(saveBookstore.getBookstoreId())
                .build();
    }

    @Transactional
    public BookstoreResponseDto.BookstoreCreateDto updateBookstore(Long bookstoreId, BookstoreRequestDto.BookstoreUpdateDto updateDto){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(()->new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));

        if (updateDto.getTitle() != null){
            findBookstore.setTitle(updateDto.getTitle());
        }
        if (updateDto.getBody() != null){
            findBookstore.setBody(updateDto.getBody());
        }
        if (updateDto.getScore() != null){
            findBookstore.setScore(updateDto.getScore());
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
    public Long deleteBookstore(Long bookstoreId){

        Bookstore findBookstore = bookstoreRepository.findById(bookstoreId).orElseThrow(() -> new BookstoreHandler(ErrorStatus.BOOKSTORE_NOT_FOUND));


        bookstoreRepository.delete(findBookstore);

        return bookstoreId;
    }


    // 책방 목록 조회
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstores(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = new ArrayList<>();

        findBookstores.forEach(bs -> {
            resultDtos.add(new BookstoreResponseDto.BookstoreListResultDto(bs.getBookstoreId(), bs.getTitle(), bs.getBody(), bs.getScore(), bs.getFairytale().getFairytaleId()));
        });


        return resultDtos;
    }

    // 책방 목록 조회 - 최신순
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstoresByDate(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = findBookstores.stream()
                .sorted(Comparator.comparing(Bookstore::getCreatedAt).reversed())
                .map(bs -> new BookstoreResponseDto.BookstoreListResultDto(bs.getBookstoreId(), bs.getTitle(), bs.getBody(), bs.getScore(), bs.getFairytale().getFairytaleId()))
                .collect(Collectors.toList());

        return resultDtos;
    }

    // 책방 목록 조회 - 댓글순
    public List<BookstoreResponseDto.BookstoreListResultDto> getBookstoresByComments(){

        List<Bookstore> findBookstores = bookstoreRepository.findAll();

        findBookstores = findBookstores.stream().sorted(Comparator.comparing(Bookstore::getCommentCount).reversed()).collect(Collectors.toList());

        List<BookstoreResponseDto.BookstoreListResultDto> resultDtos = new ArrayList<>();

        findBookstores.forEach(bs -> {
            resultDtos.add(new BookstoreResponseDto.BookstoreListResultDto(bs.getBookstoreId(), bs.getTitle(), bs.getBody(), bs.getScore(), bs.getFairytale().getFairytaleId()));
        });

        return resultDtos;
    }

}
