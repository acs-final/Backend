package fairytale;


import com.common.entity.Books;
import com.common.entity.BooksGenre;
import com.common.repository.BooksRepository;
import fairytale.dto.books.BooksResponseDto;

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

    public List<BooksResponseDto.RecommendedBook> getRecommendedBooks(BooksGenre genre){

        List<Books> findBooks = booksRepository.findAllByGenre(genre);

        findBooks = findBooks.stream().sorted(Comparator.comparing(Books::getScore).reversed()).toList();

        List<BooksResponseDto.RecommendedBook> topBooks = findBooks.stream()
                .map(b ->
                        new BooksResponseDto.RecommendedBook(b.getTitle(), b.getAuthor(),b.getPrice(), b.getScore(), b.getImageUrl(), b.getGoodsUrl())
                )
                .limit(3)
                .toList();


        return topBooks;
    }


}
