package acs.aws_final_project.domain.fairyTale.service;


import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FairyTaleService {

    private final PollyService pollyService;
    private final StableDiffusionService stableDiffusionService;


    public List<FairyTaleResponseDto.PollyResultDto> asyncPolly(List<FairyTaleRequestDto.PollyRequestDto> requestDtos){


        List<FairyTaleRequestDto.PollyRequestDto> requestIds = requestDtos;

        log.info("requestIds: {}", requestDtos);

        //List<Integer> requestIds = List.of(1, 2, 3, 4, 5);

        // 요청을 비동기적으로 실행
        List<CompletableFuture<String>> futures = requestIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> pollyService.createMP3(id)))  // 비동기 실행하는 부분.
                .collect(Collectors.toList());

        // 모든 요청이 완료될 때까지 기다리고 순서 보장
        List<String> results = futures.stream()
                .map(CompletableFuture::join) // 응답 순서 보장됨
                .toList();

        // 결과 출력
        results.forEach(System.out::println);

        List<FairyTaleResponseDto.PollyResultDto> collect = results.stream().map(FairyTaleResponseDto.PollyResultDto::new).collect(Collectors.toList());

        log.info("collect: {}", collect);

        return collect;
    }


    public List<FairyTaleResponseDto.StablediffusionResultDto> acyncImage(List<FairyTaleRequestDto.StablediffusionRequestDto> requestDtos) throws JsonProcessingException{

        List<FairyTaleRequestDto.StablediffusionRequestDto> requestIds = requestDtos;

        // 🔥 1. 비동기적으로 외부 API 호출 (이미지 생성)
        List<CompletableFuture<String>> imageFutures = requestIds.stream()
                .map(prompt -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return stableDiffusionService.createImage(prompt.getTitle(),prompt.getFileName() , prompt.getPrompt());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })) // 병렬 실행
                .toList();


        // 🔥 2. 모든 이미지 생성이 완료될 때까지 기다리기 (순서 보장)
        List<String> images = imageFutures.stream()
                .map(CompletableFuture::join)  // join()을 사용하여 순서 보장
                .toList();

        List<FairyTaleResponseDto.StablediffusionResultDto> resultDtos = new ArrayList<>();

        images.forEach(i -> {
            resultDtos.add(new FairyTaleResponseDto.StablediffusionResultDto(i));
        });

        return resultDtos;



        // 🔥 3. 비동기적으로 S3에 업로드 (이미지 순서 유지)
//        List<CompletableFuture<String>> uploadFutures = images.stream()
//                .map(image -> CompletableFuture.supplyAsync(() -> s3Uploader.uploadToS3(image)))
//                .collect(Collectors.toList());

        // 🔥 4. 모든 업로드 완료 후 S3 URL 리스트 반환



    }

}
