package acs.aws_final_project.domain.bookstore;

import acs.aws_final_project.domain.fairyTale.FairyTale;

public class BookstoreConverter {

    public static Bookstore toBookstore(String title, String body, Float score, FairyTale fairyTale){

        return Bookstore.builder()
                .title(title)
                .body(body)
                .score(score)
                .fairyTale(fairyTale)
                .build();
    }
}
