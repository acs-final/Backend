package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.member.Member;
import software.amazon.awssdk.services.polly.endpoints.internal.Value;

public class BookstoreConverter {

    public static Bookstore toBookstore(Member member, String title, String body, Float score, Integer commentCount, Fairytale fairyTale, String imageUrl){

        return Bookstore.builder()
                .member(member)
                .title(title)
                .body(body)
                .score(score)
                .commentCount(commentCount)
                .fairytale(fairyTale)
                .imageUrl(imageUrl)
                .build();
    }
}
