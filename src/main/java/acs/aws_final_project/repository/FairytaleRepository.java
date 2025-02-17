package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairytaleRepository extends JpaRepository<Fairytale, Long> {

    List<Fairytale> findAllByMember(Member member);

    @Query("SELECT f FROM Fairytale f ORDER BY f.avgScore DESC LIMIT 3")
    List<Fairytale> findAllOfTop3();

    long countByGenre(String genre);
}
