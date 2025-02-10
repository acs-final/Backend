package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.bookstore.dto.BookstoreRequestDto;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.domain.fairyTale.FairyTale;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.BookstoreHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookstoreService {

    private final BookstoreRepository bookstoreRepository;
    private final FairyTaleRepository fairyTaleRepository;

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

        FairyTale findFairyTale = fairyTaleRepository.findByTitle(createDto.getFairytaleTitle());

        Bookstore newBookstore = BookstoreConverter.toBookstore(createDto.getTitle(), createDto.getBody(), createDto.getScore(), findFairyTale);

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



}
