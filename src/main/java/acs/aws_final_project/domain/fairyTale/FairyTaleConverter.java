package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.body.Body;
import acs.aws_final_project.domain.keyword.Keyword;

import java.security.Key;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class FairyTaleConverter {


    public static FairyTale toFairyTale(String title){



        return FairyTale.builder()
                .title(title)
                .build();

    }




    public static Body toBody(String content, Integer pageNumber, FairyTale fairyTale){


        return Body.builder()
                .content(content)
                .pageNumber(pageNumber)
                .fairyTale(fairyTale)
                .build();
    }

    public static Keyword toKeyword(String keyword, FairyTale fairyTale){


        return Keyword.builder()
                .fairyTale(fairyTale)
                .keyword(keyword)
                .build();
    }



}
