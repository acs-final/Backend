package acs.aws_final_project.domain.books;

import acs.aws_final_project.domain.books.dto.BooksResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
                        new BooksResponseDto.RecommendedBook(b.getTitle(), b.getAuthor(),b.getPrice(), b.getScore(), b.getImageUrl())
                )
                .limit(3)
                .toList();


        return topBooks;
    }


}
