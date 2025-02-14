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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookstore")
public class BookstoreController {

    private final BookstoreService bookstoreService;

    @PostMapping("/")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> createBookstore(@RequestHeader("memberId") String memberId, @RequestBody BookstoreRequestDto.BookstoreCreateDto createDto){

        log.info("createBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreCreateDto result = bookstoreService.createBookstore(memberId, createDto);


        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreResultDto> getBookstore(@PathVariable("bookstoreId") Long bookstoreId){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreResultDto result = bookstoreService.getBookstore(bookstoreId);

        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> updateBookstore(@RequestHeader("memberId") String memberId, @PathVariable("bookstoreId") Long bookstoreId, @RequestBody BookstoreRequestDto.BookstoreUpdateDto updateDto){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        BookstoreResponseDto.BookstoreCreateDto result = bookstoreService.updateBookstore(memberId, bookstoreId, updateDto);

        return ApiResponse.onSuccess(result);
    }

    @DeleteMapping("/{bookstoreId}")
    public ApiResponse<BookstoreResponseDto.BookstoreCreateDto> deleteBookstore(@RequestHeader("memberId") String memberId, @PathVariable("bookstoreId") Long bookstoreId){

        log.info("getBookstore API Request time: {}", LocalDateTime.now());

        Long id = bookstoreService.deleteBookstore(memberId, bookstoreId);

        BookstoreResponseDto.BookstoreCreateDto result = BookstoreResponseDto.BookstoreCreateDto.builder()
                .bookstoreId(id)
                .build();

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/")
    public ApiResponse<List<BookstoreResponseDto.BookstoreListResultDto>> getBookstores(){

        log.info("getBookstores API Request time: {}", LocalDateTime.now());

        List<BookstoreResponseDto.BookstoreListResultDto> result = bookstoreService.getBookstores();

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/latest")
    public ApiResponse<List<BookstoreResponseDto.BookstoreListResultDto>> getBookstoresByDate(){

        log.info("getBoostoresByDate API Request time: {}", LocalDateTime.now());

        List<BookstoreResponseDto.BookstoreListResultDto> result = bookstoreService.getBookstoresByDate();

        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/comments")
    public ApiResponse<List<BookstoreResponseDto.BookstoreListResultDto>> getBookstoresByComments(){

        log.info("getBookstoresByComments API Request time: {}", LocalDateTime.now());

        List<BookstoreResponseDto.BookstoreListResultDto> result = bookstoreService.getBookstoresByComments();

        return ApiResponse.onSuccess(result);
    }


}
