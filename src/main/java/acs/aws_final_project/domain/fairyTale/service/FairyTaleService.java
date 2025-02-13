package acs.aws_final_project.domain.fairyTale.service;

import acs.aws_final_project.domain.audio.Audio;
import acs.aws_final_project.domain.audio.AudioRepository;
import acs.aws_final_project.domain.body.Body;
import acs.aws_final_project.domain.body.BodyConverter;
import acs.aws_final_project.domain.body.BodyRepository;
import acs.aws_final_project.domain.books.dto.BooksResponseDto;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleRequestDto;
import acs.aws_final_project.domain.fairyTale.dto.FairyTaleResponseDto;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.image.ImageRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.FairytaleHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
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
    private final FairyTaleRepository fairyTaleRepository;
    private final ImageRepository imageRepository;
    private final AudioRepository audioRepository;
    private final BodyRepository bodyRepository;

    public List<FairyTaleResponseDto.FairyTaleListDto> getFairyTaleList() {
        List<Fairytale> findFairyTaleList = fairyTaleRepository.findAll();
        List<FairyTaleResponseDto.FairyTaleListDto> fairyTaleListDtos = findFairyTaleList.stream()
                .map(ft -> new FairyTaleResponseDto.FairyTaleListDto(ft.getFairytaleId(), ft.getTitle()))
                .toList();
        return fairyTaleListDtos;
    }

    public FairyTaleResponseDto.FairyTaleResultDto getFairyTale(Long fairytaleId) {
        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId)
                .orElseThrow(() -> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        List<Body> findBody = bodyRepository.findAllByFairytale(findFairytale);
        List<Image> findImages = imageRepository.findAllByFairytale(findFairytale);
        List<FairyTaleResponseDto.StablediffusionResultDto> myImages = findImages.stream()
                .map(i -> new FairyTaleResponseDto.StablediffusionResultDto(i.getImageUrl()))
                .toList();
        List<Audio> findAudios = audioRepository.findAllByFairytale(findFairytale);
        List<FairyTaleResponseDto.PollyResultDto> myMp3s = findAudios.stream()
                .map(a -> new FairyTaleResponseDto.PollyResultDto(a.getAudioUrl()))
                .toList();

        return FairyTaleResponseDto.FairyTaleResultDto.builder()
                .fairytaleId(fairytaleId)
                .title(findFairytale.getTitle())
                .score(findFairytale.getScore())
                .genre(findFairytale.getGenre())
                .body(BodyConverter.toBodies(findBody))
                .imageUrl(myImages)
                .mp3Url(myMp3s)
                .build();
    }

    public List<FairyTaleResponseDto.PollyResultDto> asyncPolly(List<FairyTaleRequestDto.PollyRequestDto> requestDtos, Fairytale fairytale) {
        List<FairyTaleRequestDto.PollyRequestDto> requestIds = requestDtos;
        log.info("requestIds: {}", requestDtos);

        // 비동기적으로 요청 실행
        List<CompletableFuture<String>> futures = requestIds.stream()
                .map(id -> CompletableFuture.supplyAsync(() -> pollyService.createMP3(id)))
                .collect(Collectors.toList());

        // 모든 요청이 완료될 때까지 대기 및 순서 보장
        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        results.forEach(System.out::println);

        List<FairyTaleResponseDto.PollyResultDto> collect = new ArrayList<>();
        results.forEach(r -> {
            Audio newAudio = Audio.builder()
                    .audioUrl(r)
                    .fairytale(fairytale)
                    .build();
            audioRepository.save(newAudio);
            collect.add(new FairyTaleResponseDto.PollyResultDto(r));
        });
        log.info("collect: {}", collect);
        return collect;
    }

    public List<FairyTaleResponseDto.StablediffusionResultDto> asyncImage(List<FairyTaleRequestDto.StablediffusionRequestDto> requestDtos, Fairytale fairytale) throws JsonProcessingException {
        List<FairyTaleRequestDto.StablediffusionRequestDto> requestIds = requestDtos;

        // 비동기적으로 이미지 생성 API 호출
        List<CompletableFuture<String>> imageFutures = requestIds.stream()
                .map(prompt -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return stableDiffusionService.createImage(prompt.getTitle(), prompt.getFileName(), prompt.getPrompt());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .toList();

        // 모든 이미지 생성 완료 후 결과 수집 (순서 보장)
        List<String> images = imageFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<FairyTaleResponseDto.StablediffusionResultDto> resultDtos = new ArrayList<>();
        images.forEach(i -> {
            Image newImage = Image.builder()
                    .imageUrl(i)
                    .fairytale(fairytale)
                    .build();
            imageRepository.save(newImage);
            resultDtos.add(new FairyTaleResponseDto.StablediffusionResultDto(i));
        });
        return resultDtos;
    }

    public List<FairyTaleResponseDto.Top5> getTop5() {
        List<Fairytale> findFairytale = fairyTaleRepository.findAllOfTop5();
        findFairytale = findFairytale.stream()
                .sorted(Comparator.comparing(Fairytale::getScore).reversed())
                .toList();

        List<FairyTaleResponseDto.Top5> topFairytale = findFairytale.stream()
                .map(ft -> {
                    Image findImage = imageRepository.findFirstByFairytale(ft);
                    return new FairyTaleResponseDto.Top5(ft.getFairytaleId(), ft.getTitle(), findImage.getImageUrl());
                })
                .limit(5)
                .toList();

        return topFairytale;
    }

    @Transactional
    public FairyTaleResponseDto.FairyTaleListDto grantScore(Long fairytaleId, Float score) {
        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId)
                .orElseThrow(() -> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));
        Float totalScore = (findFairytale.getScore() + score) / 2;
        findFairytale.setScore(totalScore);
        return new FairyTaleResponseDto.FairyTaleListDto(findFairytale.getFairytaleId(), findFairytale.getTitle());
    }

    // 삭제 메서드: 예외 발생 여부와 상관없이 항상 성공하도록 처리 (소프트 딜리트)
    @Transactional
    public void deleteFairytale(Long fairytaleId) {
        try {
            Fairytale fairytale = fairyTaleRepository.findById(fairytaleId).orElse(null);
            if (fairytale != null) {
                fairyTaleRepository.delete(fairytale);
            }
        } catch (Exception e) {
            log.error("Error while deleting fairytale with id {}: {}", fairytaleId, e.getMessage());
            // 예외를 swallow하여 무조건 성공하도록 함.
        }
    }
}
