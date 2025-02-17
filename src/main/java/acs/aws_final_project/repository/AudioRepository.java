package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Audio;
import acs.aws_final_project.entity.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {

    List<Audio> findAllByFairytale(Fairytale fairytale);

}
