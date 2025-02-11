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
    public static class FairyTaleListDto{

        private Long fairytaleId;
        private String title;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleResultDto{

        private Long fairytaleId;
        private String title;
        private TreeMap<String, String> body;
        //private List<Pages> body;
        private List<StablediffusionResultDto> imageUrl;
        private List<PollyResultDto> mp3Url;

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
    public static class StablediffusionResultDto{
        //private Integer pageNumber;
        private String imageUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class PollyResultDto{
        private String mp3Url;
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Top5{
        private Long fairytaleId;
        private String title;
        private String imageUrl;

    }

}
