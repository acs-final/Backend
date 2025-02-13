package acs.aws_final_project.domain.body;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BodyConverter {

    public static TreeMap<String, String> toBodies(List<Body> findBody){
        StringBuilder resultPage = new StringBuilder();
        List<String> pages = findBody.stream().map(Body::getContent).toList();
        TreeMap<String, String> resultBody = new TreeMap<>();
        int j = 0;

        for (int i=0; i< pages.size(); i++){

            resultPage.append(pages.get(i));

            if (i%2==1){
                j++;
                String key = "page" + j;
                resultBody.put(key, String.valueOf(resultPage));

                resultPage = new StringBuilder("");

            }
        }

//        findBody.forEach(b -> {
//            String page = "page" + b.getPageNumber().toString();
//            resultBody.put(page, b.getContent());
//        });

        return resultBody;

    }
}
