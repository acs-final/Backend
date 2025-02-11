package acs.aws_final_project.domain.image;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {


    Image findFirstByFairytale(Fairytale fairytale);

    List<Image> findAllByFairytale(Fairytale fairytaleId);
}
