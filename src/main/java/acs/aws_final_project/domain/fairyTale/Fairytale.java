package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.audio.Audio;
import acs.aws_final_project.domain.body.Body;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.keyword.Keyword;
import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.domain.prompt.Prompt;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "fairytale")
@AllArgsConstructor
@NoArgsConstructor
public class Fairytale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="fairytale_id")
    private Long fairytaleId;

    private String title;

    private Float score;

    private String genre;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Body> body;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Keyword> keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    private Report report;

    @OneToOne
    private Bookstore bookstore;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Prompt> prompts;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Image> images;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Audio> audio;

}
