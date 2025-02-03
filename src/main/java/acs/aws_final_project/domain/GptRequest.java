package acs.aws_final_project.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GptRequest {

    private String model;
    private List<Messages> messages;
    private HashMap<String, String> response_format;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Messages {
        private String role;
        private String content;
    }
}

