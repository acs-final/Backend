package acs.aws_final_project.repository;

import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.entity.Member;
import acs.aws_final_project.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findByFairyTale(Fairytale fairytale);

    List<Report> findAllByMember(Member member);

}
