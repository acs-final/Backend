package bookstore;


import com.common.entity.Bookstore;
import com.common.entity.Fairytale;
import com.common.entity.Member;

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
