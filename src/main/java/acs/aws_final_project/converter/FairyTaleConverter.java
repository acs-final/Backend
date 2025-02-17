package acs.aws_final_project.converter;

import acs.aws_final_project.entity.Body;
import acs.aws_final_project.dto.fairyTale.FairyTaleRequestDto;
import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.entity.Member;

public class FairyTaleConverter {


    public static Fairytale toFairyTale(Member member, String title, Float avgScore, Float totalScore, Integer scoreCount, String genre){



        return Fairytale.builder()
                .member(member)
                .title(title)
                .avgScore(avgScore)
                .totalScore(totalScore)
                .scoreCount(scoreCount)
                .genre(genre)
                .build();

    }




    public static Body toBody(String content, Integer pageNumber, Fairytale fairyTale){


        return Body.builder()
                .content(content)
                .pageNumber(pageNumber)
                .fairytale(fairyTale)
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
