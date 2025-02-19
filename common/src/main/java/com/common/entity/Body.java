package com.common.entity;


import com.common.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "body")
@AllArgsConstructor
@NoArgsConstructor
public class Body extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="body_id")
    private Long bodyId;

    private String content;

    private Integer pageNumber;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairytale_id")
    private Fairytale fairytale;



}
