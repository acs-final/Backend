package acs.aws_final_project.domain.books;

import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.scheduling.support.SimpleTriggerContext;

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



}
