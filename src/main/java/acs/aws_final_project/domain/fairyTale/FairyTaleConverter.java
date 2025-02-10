package acs.aws_final_project.domain.fairyTale;

import acs.aws_final_project.domain.body.Body;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.keyword.Keyword;

import java.security.Key;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class FairyTaleConverter {


    public static FairyTale toFairyTale(String title){



        return FairyTale.builder()
                .title(title)
                .build();

    }




    public static Body toBody(String content, Integer pageNumber, FairyTale fairyTale){


        return Body.builder()
                .content(content)
                .pageNumber(pageNumber)
                .fairyTale(fairyTale)
                .build();
    }

    public static FairyTaleRequestDto.StablediffusionRequestDto toImageRequestDto(String title, String fileName, String prompt){


        return FairyTaleRequestDto.StablediffusionRequestDto.builder()
                .title(title)
                .fileName(fileName)
                .prompt(prompt)
                .build();
    }

    public static FairyTaleRequestDto.PollyRequestDto toMp3RequestDto(String title, String fileName, String text){


        return FairyTaleRequestDto.PollyRequestDto.builder()
                .title(title)
                .fileName(fileName)
                .text(text)
                .build();
    }

}
