package acs.aws_final_project.domain.keyword;

import acs.aws_final_project.domain.fairyTale.FairyTale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {


    List<Keyword> findAllByFairyTale(FairyTale fairyTale);

}
