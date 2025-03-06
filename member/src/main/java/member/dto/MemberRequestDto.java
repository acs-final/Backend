package member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class UpdateProfileDto{

        private String nickname;
        private String username;
        private Integer childAge;
        private String color;
    }


}
