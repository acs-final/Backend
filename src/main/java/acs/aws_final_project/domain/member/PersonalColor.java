package acs.aws_final_project.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum PersonalColor {
    PINK("#FFB6C1"),
    BLUE("#AEC6CF"),
    MINT("#98FB98"),
    PURPLE("#D8BFD8"),
    YELLOW("#FAFAD2"),
    ORANGE("#FFDAB9"),
    GREEN("#77DD77"),
    GREY("#D3D3D3");


    private final String value;


    @Override
    public String toString() {
        return this.value;
    }
}
