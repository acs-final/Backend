package com.common.repository;

import com.common.entity.Books;
import com.common.entity.BooksGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Books, Long> {

    List<Books> findAllByGenre(BooksGenre genre);
}
