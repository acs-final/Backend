package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.report.Report;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.member.dto.MemberResponseDto;

public class MemberConverter {

    public static Member toMember(String memberId, Integer credit){

        return Member.builder()
                .memberId(memberId)
                .credit(credit)
                .loginStatus(LoginStatus.ACTIVE)
                .build();
    }

    public static MemberResponseDto.MyFairytaleDto toMyFairytale(Member member, Fairytale fairytale, Boolean hasReport, Image image){

        return MemberResponseDto.MyFairytaleDto.builder()
                .fairytaleId(fairytale.getFairytaleId())
                .createdAt(fairytale.getCreatedAt().toLocalDate())
                .writer(member.getName())
                .title(fairytale.getTitle())
                .genre(fairytale.getGenre())
                .hasReport(hasReport)
                .score(fairytale.getAvgScore())
                .imageUrl(image.getImageUrl())
                .build();

    }

    public static MemberResponseDto.MyBookstoreDto toMyBookstore(Member member, Bookstore bookstore, Fairytale fairytale){

        return MemberResponseDto.MyBookstoreDto.builder()
                .bookstoreId(bookstore.getBookstoreId())
                .writer(member.getMemberId())
                .title(bookstore.getTitle())
                .genre(fairytale.getGenre())
                .commentCount(bookstore.getCommentCount())
                .score(bookstore.getAvgScore())
                .createdAt(bookstore.getCreatedAt().toLocalDate())
                .fairytaleId(fairytale.getFairytaleId())
                .build();
    }

    public static MemberResponseDto.MyReportDto toMyReport(Member member, Report report, Fairytale fairytale){

        return MemberResponseDto.MyReportDto.builder()
                .reportId(report.getReportId())
                .writer(member.getName())
                .title(report.getTitle())
                .genre(fairytale.getGenre())
                .score(report.getScore())
                .createdAt(report.getCreatedAt().toLocalDate())
                .fairytaleId(fairytale.getFairytaleId())
                .build();
    }
}
