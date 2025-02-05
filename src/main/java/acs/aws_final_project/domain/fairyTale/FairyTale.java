package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.member.Member;
import jakarta.persistence.*;
import org.springframework.data.annotation.TypeAlias;

import java.util.List;

@Entity
@Table(name="fairytale")
public class FairyTale {

    @Id
    private Long fairytaleId;

    private String title;

//    @OneToMany
//    private List<String> body;
//
//    @OneToMany
//    private List<String> keywords;
//
//    @ManyToOne
//    private Member member;
}
