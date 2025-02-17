package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    Image findFirstByFairytale(Fairytale fairytale);

    List<Image> findAllByFairytale(Fairytale fairytaleId);
}
