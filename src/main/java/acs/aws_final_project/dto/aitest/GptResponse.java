package acs.aws_final_project.dto.aitest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GptResponse {

    private List<Choice> choices;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private GptRequest.Messages message;
    }
}