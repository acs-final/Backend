package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairyTaleRepository extends JpaRepository<Fairytale, Long> {

    Fairytale findByFairytaleId(Long fairytaleId);
    Fairytale findByTitle(String title);

    List<Fairytale> findAllByMember(Member member);

    @Query("SELECT f FROM Fairytale f ORDER BY f.avgScore DESC LIMIT 3")
    List<Fairytale> findAllOfTop3();

    long countByGenre(String genre);


//    long count();
}
