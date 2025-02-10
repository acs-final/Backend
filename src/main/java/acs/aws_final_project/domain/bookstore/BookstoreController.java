package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.bookstore.dto.BookstoreRequestDto;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookstore")
public class BookstoreController {

    private final BookstoreService bookstoreService;

    @PostMapping("/")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> createBookstore(@RequestBody BookstoreRequestDto.BookstoreCreateDto createDto){

        log.info("createBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreCreateDto result = bookstoreService.createBookstore(createDto);


        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreResultDto> getBookstore(@PathVariable("bookstoreId") Long bookstoreId){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreResultDto result = bookstoreService.getBookstore(bookstoreId);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> updateBookstre(@PathVariable Long bookstoreId, @RequestBody BookstoreRequestDto.BookstoreUpdateDto updateDto){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreCreateDto result = bookstoreService.updateBookstore(bookstoreId, updateDto);

        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> deleteBookstore(@PathVariable Long bookstoreId){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        Long id = bookstoreService.deleteBookstore(bookstoreId);

        BookstoreResponseDto.BookstoreCreateDto result = BookstoreResponseDto.BookstoreCreateDto.builder()
                .bookstoreId(id)
                .build();

        return ApiResponse.onSuccess(result);
    }
}
