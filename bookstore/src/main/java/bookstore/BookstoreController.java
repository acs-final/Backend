package bookstore;

import bookstore.dto.books.BooksResponseDto;
import bookstore.dto.bookstore.BookstoreRequestDto;
import bookstore.dto.bookstore.BookstoreResponseDto;

import bookstore.service.BooksService;
import bookstore.service.BookstoreService;

import com.common.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/bookstore")
public class BookstoreController {

    private final BookstoreService bookstoreService;

    private final BooksService booksService;


    @GetMapping("/recommanded/")
    public ApiResponse<List<BooksResponseDto.RecommendedBook>> getRecommendedBooks(@RequestParam("genre") String keyword){

        log.info("getRecommendedBooks API Request time: {}", LocalDateTime.now());

        List<BooksResponseDto.RecommendedBook> result = booksService.getRecommendedBooks(keyword);

        return ApiResponse.onSuccess(result);
    }

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
