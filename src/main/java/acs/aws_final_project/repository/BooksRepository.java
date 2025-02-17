package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Books;
import acs.aws_final_project.entity.BooksGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

    List<Books> findAllByGenre(BooksGenre genre);
}
