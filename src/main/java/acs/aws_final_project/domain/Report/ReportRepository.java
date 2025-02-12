package acs.aws_final_project.domain.Report;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findByFairyTale(Fairytale fairytale);

}
