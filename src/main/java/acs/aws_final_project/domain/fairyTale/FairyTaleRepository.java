package acs.aws_final_project.domain.fairyTale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FairyTaleRepository extends JpaRepository<Fairytale, Long> {

    Fairytale findByTitle(String title);

    @Query("SELECT f FROM Fairytale f ORDER BY f.score DESC LIMIT 5")
    List<Fairytale> findAllOfTop5();
}
