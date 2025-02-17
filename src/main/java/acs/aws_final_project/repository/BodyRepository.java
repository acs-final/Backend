package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Body;
import acs.aws_final_project.entity.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BodyRepository extends JpaRepository<Body, Long> {

    List<Body> findAllByFairytale(Fairytale fairytale);

}
