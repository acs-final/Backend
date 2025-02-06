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

        private Long fairytaleId;
        private String title;
        private TreeMap<String, String> body;
        //private List<Pages> body;
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
    public static class PollyResultDto{
        private String mp3Url;
    }


}
