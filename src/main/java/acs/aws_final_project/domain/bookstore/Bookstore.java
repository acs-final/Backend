package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.comment.Comment;
import acs.aws_final_project.domain.fairyTale.FairyTale;
import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "bookstore")
@AllArgsConstructor
@NoArgsConstructor
public class Bookstore extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bookstore_id")
    private Long bookstoreId;


    private String title;

    private String body;

    private Float score;

    @OneToOne
    private FairyTale fairyTale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL)
    private List<Comment> comments;




}

