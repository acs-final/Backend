package acs.aws_final_project.domain.body;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.TreeMap;

public class BodyConverter {

    public static TreeMap<String, String> toBodies(List<Body> findBody){

        TreeMap<String, String> resultBody = new TreeMap<>();
        findBody.forEach(b -> {
            String page = "page" + b.getPageNumber().toString();
            resultBody.put(page, b.getContent());
        });

        return resultBody;

    }
}
