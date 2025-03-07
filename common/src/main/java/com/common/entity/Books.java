package com.common.entity;


import com.common.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
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

    private String keyword;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String goodsUrl; // 사이트 링크.

}
