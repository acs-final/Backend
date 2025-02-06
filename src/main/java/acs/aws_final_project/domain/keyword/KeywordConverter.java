package acs.aws_final_project.domain.keyword;

import software.amazon.awssdk.services.bedrockruntime.endpoints.internal.Value;

import java.util.ArrayList;
import java.util.List;

public class KeywordConverter {

    public static List<String> toKeywords(List<Keyword> findKeyword){

        List<String> keywords = new ArrayList<>();

        findKeyword.forEach(k -> {
            keywords.add(k.getKeyword());
        });

        return keywords;
    }
}
