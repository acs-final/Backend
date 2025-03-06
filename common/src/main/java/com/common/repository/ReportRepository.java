package com.common.repository;

import com.common.entity.Fairytale;
import com.common.entity.Member;
import com.common.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findByFairyTale(Fairytale fairytale);

    List<Report> findAllByMember(Member member);

}
