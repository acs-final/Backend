package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookstoreRepository extends JpaRepository<Bookstore, Long> {

    List<Bookstore> findAllByMember(Member member);

}
