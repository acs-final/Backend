package acs.aws_final_project.converter;

import acs.aws_final_project.entity.Body;

import java.util.List;
import java.util.TreeMap;

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
