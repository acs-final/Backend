package acs.aws_final_project.entity;

import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
public class Books extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="book_id")
    private Long bookId;

    private String title;

    private String author;

    private Integer price;

    private Float score;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private BooksGenre genre;

    private String goodsUrl; // 사이트 링크.

}
