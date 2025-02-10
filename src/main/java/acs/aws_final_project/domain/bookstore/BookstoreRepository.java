package acs.aws_final_project.domain.bookstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore, Long> {


}
