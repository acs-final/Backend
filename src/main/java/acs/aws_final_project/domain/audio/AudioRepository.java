package acs.aws_final_project.domain.audio;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findAllByFairytale(Fairytale fairytale);

}
