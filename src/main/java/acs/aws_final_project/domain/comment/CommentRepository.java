package acs.aws_final_project.domain.comment;

import acs.aws_final_project.domain.bookstore.Bookstore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByBookstore(Bookstore bookstore);
}
