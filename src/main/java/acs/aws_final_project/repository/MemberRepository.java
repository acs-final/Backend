package acs.aws_final_project.repository;

import acs.aws_final_project.entity.LoginStatus;
import acs.aws_final_project.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

//    @Query(value = "Select * From member Where login_status=INACTIVE", nativeQuery = true)
//    Optional<Member> findById(String memberId);


    //@Query("SELECT m FROM Member m where m.memberId = :memberId and m.loginStatus = 'INACTIVE'")
    @Query(value = "SELECT * FROM member WHERE member_id = :memberId AND login_status = 'INACTIVE'", nativeQuery = true)
    Optional<Member> findInactiveMemberById(@Param("memberId") String memberId);

    Optional<Member> findByLoginStatus(LoginStatus loginStatus);

    long countByLastVisit(LocalDate localDate);

//    @Query("SELECT COUNT(DISTINCT m.memberId) FROM Member m WHERE m.lastVisit BETWEEN :startDate AND :endDate")
//    long countByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT COUNT(DISTINCT m.memberId) FROM Member m WHERE FUNCTION('YEAR', m.lastVisit) = :year AND FUNCTION('MONTH', m.lastVisit) = :month")
    long countByMonth(@Param("year") int year, @Param("month") int month);
}
