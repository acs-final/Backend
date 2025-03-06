package com.common.entity;


import com.common.global.baseEntity.BaseEntity;
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
//@Where(clause = "is_deleted = 'ACTIVE'")
//@SQLDelete(sql =  "UPDATE fairytale  SET is_deleted = 'INACTIVE' WHERE fairytale_id = ?")
public class Fairytale extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="fairytale_id")
    private Long fairytaleId;

    private String title;

    private Float avgScore;

    private Float totalScore;

    private Integer scoreCount;

    private String genre;

    private Long likeCount;

//    @Enumerated(EnumType.STRING)
//    private IsDeleted IsDeleted;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Body> body;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    private Report report;

    @OneToOne
    private Bookstore bookstore;


    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Image> images;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Audio> audio;

    @OneToMany(mappedBy = "fairytale", cascade = CascadeType.ALL)
    private List<Likes> likes;
}
