package acs.aws_final_project.domain.books;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

    List<Books> findAllByGenre(BooksGenre genre);
}
