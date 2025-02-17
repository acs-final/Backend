package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Bookstore;
import acs.aws_final_project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore, Long> {

    List<Bookstore> findAllByMember(Member member);

}
