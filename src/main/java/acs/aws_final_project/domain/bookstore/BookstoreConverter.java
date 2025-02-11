package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.fairyTale.Fairytale;

public class BookstoreConverter {

    public static Bookstore toBookstore(String title, String body, Float score, Fairytale fairyTale, String imageUrl){

        return Bookstore.builder()
                .title(title)
                .body(body)
                .score(score)
                .fairytale(fairyTale)
                .imageUrl(imageUrl)
                .build();
    }
}
