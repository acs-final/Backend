package fairytale.dto.fairyTale;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FairyTaleRequestDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleLikesDto{

        private Long fairytaleId;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleCreateDto{

        private String genre;
        private String gender;
        private String challenge;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class StablediffusionRequestDto{

        private String title;
        private String fileName;
        private String prompt;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class PollyRequestDto{

        private String title;
        private String text;
        private String fileName;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class ScoreRequestDto{

        private Float score;

    }
}
