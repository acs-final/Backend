package acs.aws_final_project.domain.Report;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findByFairyTale(Fairytale fairytale);

    List<Report> findAllByMember(Member member);
    
}
