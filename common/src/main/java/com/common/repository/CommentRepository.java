package com.common.repository;

import com.common.entity.Bookstore;
import com.common.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBookstore(Bookstore bookstore);
}
