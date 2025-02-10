package acs.aws_final_project.domain.books;

import acs.aws_final_project.domain.books.dto.BooksResponseDto;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.bookstore.dto.BookstoreResponseDto;
import acs.aws_final_project.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/books")
public class BooksController {

    private final BooksService booksService;

    @GetMapping("/")
    public ApiResponse<List<BooksResponseDto.TopBook>> getTop5Books(@RequestParam BooksGenre genre){

        log.info("getTop5Books API Request time: {}", LocalDateTime.now());

        List<BooksResponseDto.TopBook> result = booksService.getTop5(genre);

        return ApiResponse.onSuccess(result);
    }

}
