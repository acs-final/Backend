package acs.aws_final_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@Table(name = "audio")
@AllArgsConstructor
@NoArgsConstructor
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="audio_id")
    private Long audioId;

    @Column(length = 500)
    private String audioUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fairytale_id")
    private Fairytale fairytale;
}
