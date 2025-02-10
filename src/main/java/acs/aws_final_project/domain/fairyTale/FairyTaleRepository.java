package acs.aws_final_project.domain.fairyTale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FairyTaleRepository extends JpaRepository<Fairytale, Long> {

    Fairytale findByTitle(String title);

}
