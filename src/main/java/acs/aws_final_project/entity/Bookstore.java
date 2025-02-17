package acs.aws_final_project.entity;

import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private Float myScore;

    private Float avgScore;

    private Float totalScore;

    private Integer commentCount;

    @Column(length = 500)
    private String imageUrl;

    @OneToOne
    private Fairytale fairytale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "bookstore", cascade = CascadeType.ALL)
    private List<Comment> comments;




}

