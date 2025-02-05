package acs.aws_final_project.domain.fairyTale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.TreeMap;

public class FairyTaleResponseDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleResultDto{

        private String title;
        private TreeMap<String, String> body;
        private TreeMap<String, String> prompt;
        private List<String> keywords;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Pages{
        private String page;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Prompt{
        private String prompt;
    }


}
