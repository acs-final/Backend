package acs.aws_final_project.service.fairytaleservice;

import acs.aws_final_project.entity.Audio;
import acs.aws_final_project.repository.AudioRepository;
import acs.aws_final_project.entity.Body;
import acs.aws_final_project.converter.BodyConverter;
import acs.aws_final_project.repository.BodyRepository;
import acs.aws_final_project.entity.Fairytale;
import acs.aws_final_project.repository.FairytaleRepository;
import acs.aws_final_project.dto.fairyTale.FairyTaleRequestDto;
import acs.aws_final_project.dto.fairyTale.FairyTaleResponseDto;
import acs.aws_final_project.entity.Image;
import acs.aws_final_project.repository.ImageRepository;
import acs.aws_final_project.repository.MemberRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.FairytaleHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final FairytaleRepository fairyTaleRepository;
    private final ImageRepository imageRepository;
    private final AudioRepository audioRepository;
    private final BodyRepository bodyRepository;
    private final MemberRepository memberRepository;

    public List<FairyTaleResponseDto.FairyTaleListDto> getFairyTaleList() {
        List<Fairytale> findFairyTaleList = fairyTaleRepository.findAll();
        List<FairyTaleResponseDto.FairyTaleListDto> fairyTaleListDtos = findFairyTaleList.stream()
                .map(ft -> new FairyTaleResponseDto.FairyTaleListDto(ft.getFairytaleId(), ft.getTitle(), ft.getAvgScore()))
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
                .score(findFairytale.getAvgScore())
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



    @Transactional
    public FairyTaleResponseDto.FairyTaleListDto grantScore(Long fairytaleId, Float score) {

        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId)
                .orElseThrow(() -> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        int scoreCount = findFairytale.getScoreCount() + 1;
        float totalScore = findFairytale.getTotalScore() + score;

        Float avgScore = totalScore / scoreCount;

        findFairytale.setAvgScore(avgScore);
        findFairytale.setTotalScore(totalScore);
        findFairytale.setScoreCount(scoreCount);

        return new FairyTaleResponseDto.FairyTaleListDto(findFairytale.getFairytaleId(), findFairytale.getTitle(), findFairytale.getAvgScore());
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

    public List<FairyTaleResponseDto.Top3> getTop3() {
        List<Fairytale> findFairytale = fairyTaleRepository.findAllOfTop3();
        findFairytale = findFairytale.stream()
                .sorted(Comparator.comparing(Fairytale::getAvgScore).reversed())
                .toList();

        List<FairyTaleResponseDto.Top3> topFairytale = findFairytale.stream()
                .map(ft -> {
                    Image findImage = imageRepository.findFirstByFairytale(ft);
                    return new FairyTaleResponseDto.Top3(ft.getFairytaleId(), ft.getTitle(), findImage.getImageUrl());
                })
                .limit(3)
                .toList();

        return topFairytale;
    }

    public FairyTaleResponseDto.Dashboard getDashboard(){

        List<Fairytale> findFairytale = fairyTaleRepository.findAllOfTop3();
        findFairytale = findFairytale.stream()
                .sorted(Comparator.comparing(Fairytale::getAvgScore).reversed())
                .toList();

        List<FairyTaleResponseDto.Top3InDashboard> top3InDashboards = findFairytale.stream()
                .map(ft -> new FairyTaleResponseDto.Top3InDashboard(ft.getTitle(), ft.getAvgScore()))
                .limit(3)
                .toList();

        List<String> genres = List.of("한국 전래 동화", "세계 전래 동화", "판타지 동화", "동물 동화", "가족 동화", "의사 직업 동화", "소방관 직업 동화", "경찰 직업 동화");

        List<FairyTaleResponseDto.CountByGenre> countByGenres = genres.stream().map(g -> {
            long count = fairyTaleRepository.countByGenre(g);
            return new FairyTaleResponseDto.CountByGenre(g, count);
        }).collect(Collectors.toList());


        List<String> localDate = List.of(LocalDate.now().toString().split("-"));
        LocalDate startDate = LocalDate.of(Integer.parseInt(localDate.get(0)), Integer.parseInt(localDate.get(1)), 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());



        FairyTaleResponseDto.Dashboard result = FairyTaleResponseDto.Dashboard.builder()
                .todayVisitor(memberRepository.countByLastVisit(LocalDate.now()))
                .monthlyVisitor(memberRepository.countByMonth(Integer.parseInt(localDate.get(0)), Integer.parseInt(localDate.get(1))))
                //.monthlyVisitor(memberRepository.countByMonth(startDate, endDate))
                .totalFairytale(fairyTaleRepository.count())
                .countByGenre(countByGenres)
                .top3(top3InDashboards)
                .build();


        return result;
    }
}
