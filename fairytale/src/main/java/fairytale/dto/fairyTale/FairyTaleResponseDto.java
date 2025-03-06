package fairytale.dto.fairyTale;

import com.common.entity.Fairytale;
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
    public static class FairyTaleImageAndMp3Dto {
        private Long fairytaleId;
        TreeMap<String, String> sortedPrompt;
        String title;
        TreeMap<String, String> resultBody;
        Fairytale myFairytale;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleListDto {
        private Long fairytaleId;
        private String title;
        private String imageUrl;
        private Long likeCount;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleLikeListDto {
        private Long fairytaleId;
        private String title;
        private String imageUrl;
        private Long likeCount;
        private Boolean isPressed;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTalePromptDto {
        private String prompt;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleCreateDto {
        private Long fairytaleId;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleLikesDto {
        private Long fairytaleId;
        private Long likeCount;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleDto {
        private Long fairytaleId;
        private String title;
        private Float score;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleResultDto {
        private Long fairytaleId;
        private String title;
        private Float score;
        private String genre;
        private Long likeCount;
        private TreeMap<String, String> body;
        //private List<Pages> body;
        private List<StablediffusionResultDto> imageUrl;
        private List<PollyResultDto> mp3Url;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Pages {
        private String page;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class StablediffusionResultDto {
        //private Integer pageNumber;
        private String imageUrl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class PollyResultDto {
        private String mp3Url;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Dashboard {

        private Long todayVisitor;
        private Long monthlyVisitor;
        private Long totalFairytale;

        private List<CountByGenre> countByGenre;
        private List<Top3InDashboard> top3;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class CountByGenre {

        private String genre;
        private Long count;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Top3InDashboard {

        private String title;
        private Float score;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class Top3 {
        private Long fairytaleId;
        private String title;
        private String imageUrl;
    }

    // 삭제 응답용 DTO: 삭제된 동화의 id만 전달
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class FairyTaleDeleteDto {
        private Long fairytaleId;
    }
}
