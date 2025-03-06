package bookstore.service;


import com.common.entity.Books;

import com.common.repository.BooksRepository;
import bookstore.dto.books.BooksResponseDto;

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
public class BooksService {

    private final BooksRepository booksRepository;

    public List<BooksResponseDto.RecommendedBook> getRecommendedBooks(String keyword){

        List<Books> findBooks = booksRepository.findAllByKeyword(keyword);

        findBooks = findBooks.stream().peek(b -> {
            if (b.getScore() == null){
                b.setScore((float) 0);
            }
        }).sorted(Comparator.comparing(Books::getScore).reversed()).toList();

        List<BooksResponseDto.RecommendedBook> topBooks = findBooks.stream()
                .map(b ->
                        new BooksResponseDto.RecommendedBook(b.getTitle(), b.getAuthor(),b.getPrice(), b.getScore(), b.getImageUrl(), b.getGoodsUrl())
                )
                .limit(3)
                .toList();


        return topBooks;
    }


}
