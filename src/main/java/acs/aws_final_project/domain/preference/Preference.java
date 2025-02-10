package acs.aws_final_project.domain.preference;

import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Table(name = "preference")
@AllArgsConstructor
@NoArgsConstructor
public class Preference extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="preference_id")
    private Long preferenceId;

    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;






}
