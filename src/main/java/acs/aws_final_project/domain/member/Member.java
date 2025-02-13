package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.comment.Comment;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.preference.Preference;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "login_status = 'ACTIVE'")
@SQLDelete(sql =  "UPDATE member  SET login_status = 'INACTIVE' WHERE member_id = ?")
public class Member extends BaseEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private String memberId;

    private String nickname;

    private String name;

    private String identity;

    private Integer credit;

    private Integer childAge;

    @Enumerated(EnumType.STRING)
    private LoginStatus loginStatus;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Preference> preferenceList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Report> reports;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Bookstore> bookstores;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Fairytale> fairytales;



}
