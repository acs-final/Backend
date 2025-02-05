package acs.aws_final_project.domain.fairyTale.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FairyTaleRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleCreateDto{

        private String genre;
        private String gender;
        private String challenge;

    }
}
