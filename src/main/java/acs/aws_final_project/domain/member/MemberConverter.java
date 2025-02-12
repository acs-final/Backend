package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.member.dto.MemberResponseDto;

public class MemberConverter {

    public static Member toMember(String memberId, String name, Integer credit){

        return Member.builder()
                .memberId(memberId)
                .name(name)
                .credit(credit)
                .build();
    }

    public static MemberResponseDto.MyFairytaleDto toMyFairytale(Member member, Fairytale fairytale, Boolean hasReport, Image image){

        return MemberResponseDto.MyFairytaleDto.builder()
                .memberId(member.getMemberId())
                .fairytaleId(fairytale.getFairytaleId())
                .createdAt(fairytale.getCreatedAt().toLocalDate())
                .writer(member.getName())
                .title(fairytale.getTitle())
                .genre(fairytale.getGenre())
                .hasReport(hasReport)
                .score(fairytale.getScore())
                .imageUrl(image.getImageUrl())
                .build();

    }

}
