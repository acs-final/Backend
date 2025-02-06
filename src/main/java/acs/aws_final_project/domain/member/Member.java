package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.comment.Comment;
import acs.aws_final_project.domain.fairyTale.FairyTale;
import acs.aws_final_project.domain.preference.Preference;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long memberId;

    private String name;

    private String identity;

    private Integer credit;

    private Integer childAge;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Preference> preferenceList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookstore> bookstores;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<FairyTale> fairyTales;



}
