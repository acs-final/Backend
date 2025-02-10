package acs.aws_final_project.domain.prompt;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@Table(name = "prompt")
@AllArgsConstructor
@NoArgsConstructor
public class Prompt extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="prompt_id")
    private Long promptId;


    private String text;

    private Integer pageNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairytale_id")
    private Fairytale fairytale;



}
