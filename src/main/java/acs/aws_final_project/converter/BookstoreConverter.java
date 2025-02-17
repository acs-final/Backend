package acs.aws_final_project.converter;

import acs.aws_final_project.entity.Bookstore;
import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.entity.Member;

public class BookstoreConverter {

    public static Bookstore toBookstore(Member member, String title, String body, Float myScore, Float avgScore, Float totalScore, Integer commentCount, Fairytale fairyTale, String imageUrl){

        return Bookstore.builder()
                .member(member)
                .title(title)
                .body(body)
                .myScore(myScore)
                .avgScore(avgScore)
                .totalScore(totalScore)
                .commentCount(commentCount)
                .fairytale(fairyTale)
                .imageUrl(imageUrl)
                .build();
    }
}
