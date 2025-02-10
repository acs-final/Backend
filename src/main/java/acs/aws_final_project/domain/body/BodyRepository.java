package acs.aws_final_project.domain.body;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyRepository extends JpaRepository<Body, Long> {

    List<Body> findAllByFairytale(Fairytale fairytale);

}
