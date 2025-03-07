package fairytale.service;


import com.common.entity.*;
import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.FairytaleHandler;
import com.common.global.response.exception.handler.LikesHandler;
import com.common.global.response.exception.handler.MemberHandler;
import com.common.repository.*;
import fairytale.BodyConverter;

import fairytale.dto.fairyTale.FairyTaleRequestDto;
import fairytale.dto.fairyTale.FairyTaleResponseDto;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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
    private final LikesRepository likesRepository;


    @Transactional
    public FairyTaleResponseDto.FairyTaleLikesDto increaseLike(String memberId, Long fairytaleId){
        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId).orElseThrow(()-> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        if (likesRepository.existsByFairytaleAndMemberId(findFairytale, memberId)){
            throw new LikesHandler(ErrorStatus.LIKE_IS_PRESSED);
        }

        Likes newLikes = Likes.builder()
                .memberId(memberId)
                .fairytale(findFairytale)
                .build();

        likesRepository.save(newLikes);

        Long likeCounts = findFairytale.getLikeCount();
        findFairytale.setLikeCount(++likeCounts);
        fairyTaleRepository.save(findFairytale);

        return new  FairyTaleResponseDto.FairyTaleLikesDto(fairytaleId, likeCounts);

    }

    @Transactional
    public FairyTaleResponseDto.FairyTaleLikesDto decreaseLike(String memberId, Long fairytaleId){
        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId).orElseThrow(()-> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        Likes findLikes = likesRepository.findByFairytaleAndMemberId(findFairytale, memberId);


        if (findLikes != null){
            likesRepository.delete(findLikes);
        } else {
            throw new FairytaleHandler(ErrorStatus.LIKE_NOT_PRESSED);
        }

        Long likeCounts = findFairytale.getLikeCount();
        if (likeCounts > 0){
            findFairytale.setLikeCount(--likeCounts);
            fairyTaleRepository.save(findFairytale);
        }

        return new FairyTaleResponseDto.FairyTaleLikesDto(fairytaleId, likeCounts);
    }

    public List<FairyTaleResponseDto.FairyTaleLikeListDto> getFairyTaleListWithLikes(String memberId) {
        List<Fairytale> findFairyTaleList = fairyTaleRepository.findAll();
        List<FairyTaleResponseDto.FairyTaleLikeListDto> fairyTaleListDtos = findFairyTaleList.stream()
                .map(ft -> {
                    Long likeCount = 0L;
                    if (ft.getLikeCount() != null) {
                        likeCount = ft.getLikeCount();
                    }
                    String ftImage = "";
                    Image firstByFairytale = imageRepository.findFirstByFairytale(ft);
                    if (firstByFairytale != null) {
                        ftImage = firstByFairytale.getImageUrl();
                    }
                    boolean isPressed = false;

                    if (likesRepository.findByFairytaleAndMemberId(ft, memberId) != null){
                        isPressed = true;
                    }

                    return new FairyTaleResponseDto.FairyTaleLikeListDto(ft.getFairytaleId(), ft.getTitle(), ftImage, likeCount, isPressed);
                })
                .toList();
        return fairyTaleListDtos;
    }

    public List<FairyTaleResponseDto.FairyTaleListDto> getFairyTaleList() {
        List<Fairytale> findFairyTaleList = fairyTaleRepository.findAll();
        List<FairyTaleResponseDto.FairyTaleListDto> fairyTaleListDtos = findFairyTaleList.stream()
                .map(ft -> {
                    Long likeCount = 0L;
                    if (ft.getLikeCount() != null){
                        likeCount = ft.getLikeCount();
                    }
                    String ftImage = "";
                    Image firstByFairytale = imageRepository.findFirstByFairytale(ft);
                    if (firstByFairytale != null){
                        ftImage = firstByFairytale.getImageUrl();
                    }
                    return new FairyTaleResponseDto.FairyTaleListDto(ft.getFairytaleId(), ft.getTitle(), ftImage, likeCount);
                })
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
                .likeCount(findFairytale.getLikeCount())
                .build();
    }

    @Transactional
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

    @Transactional
    public CompletableFuture<List<FairyTaleResponseDto.PollyResultDto>> asyncPolly2(List<FairyTaleRequestDto.PollyRequestDto> requestDtos, Fairytale fairytale) {
        // 비동기적으로 mp3 생성 API 호출
        List<CompletableFuture<Pair<String, String>>> mp3Futures = requestDtos.stream()
                .map(mp3 -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String mp3Url = pollyService.createMP3(mp3);
                        return Pair.of(mp3Url, mp3.getFileName());
                    } catch (Exception e) {
                        log.error("mp3 생성 실패: {}, 오류: {}", mp3.getFileName(), e.getMessage());
                        return null;  // 실패한 경우 null 반환
                    }
                }).exceptionally(ex -> {
                    log.error("비동기 작업 중 예외 발생: {}", ex.getMessage());
                    return null;
                }))
                .toList();


        // 모든 mp3 생성 완료 후 결과 수집
        return CompletableFuture.allOf(mp3Futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> mp3Futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)  // 실패한 경우 제거
                        .map(pair -> {
                            Audio newAudio = Audio.builder()
                                    .audioUrl(pair.getFirst())
                                    .fairytale(fairytale)
                                    .build();
                            return newAudio;
                        })
                        .toList()
                )
                .thenApply(audios -> {
                    // 병렬로 이미지 저장 (batch insert 가능하도록 변경 가능)
                    audioRepository.saveAll(audios);
                    return audios.stream()
                            .map(mp3 -> new FairyTaleResponseDto.PollyResultDto(mp3.getAudioUrl()))
                            .toList();
                });
    }

    @Transactional
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
    public CompletableFuture<List<FairyTaleResponseDto.StablediffusionResultDto>> asyncImage2(
            List<FairyTaleRequestDto.StablediffusionRequestDto> requestDtos, Fairytale fairytale) {

        // 비동기적으로 이미지 생성 API 호출
        List<CompletableFuture<Pair<String, String>>> imageFutures = requestDtos.stream()
                .map(prompt -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String imageUrl = stableDiffusionService.createImage(prompt.getTitle(), prompt.getFileName(), prompt.getPrompt());
                        return Pair.of(imageUrl, prompt.getFileName());
                    } catch (Exception e) {
                        log.error("이미지 생성 실패: {}, 오류: {}", prompt.getFileName(), e.getMessage());
                        return null;  // 실패한 경우 null 반환
                    }
                }).exceptionally(ex -> {
                    log.error("비동기 작업 중 예외 발생: {}", ex.getMessage());
                    return null;
                }))
                .toList();

        // 모든 이미지 생성 완료 후 결과 수집
        return CompletableFuture.allOf(imageFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> imageFutures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)  // 실패한 경우 제거
                        .map(pair -> {
                            Image newImage = Image.builder()
                                    .imageUrl(pair.getFirst())
                                    .fairytale(fairytale)
                                    .build();
                            return newImage;
                        })
                        .toList()
                )
                .thenApply(images -> {
                    // 병렬로 이미지 저장 (batch insert 가능하도록 변경 가능)
                    imageRepository.saveAll(images);
                    return images.stream()
                            .map(img -> new FairyTaleResponseDto.StablediffusionResultDto(img.getImageUrl()))
                            .toList();
                });
    }



    @Transactional
    public FairyTaleResponseDto.FairyTaleDto grantScore(Long fairytaleId, Float score) {

        Fairytale findFairytale = fairyTaleRepository.findById(fairytaleId)
                .orElseThrow(() -> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        int scoreCount = findFairytale.getScoreCount() + 1;
        float totalScore = findFairytale.getTotalScore() + score;

        Float avgScore = totalScore / scoreCount;

        findFairytale.setAvgScore(avgScore);
        findFairytale.setTotalScore(totalScore);
        findFairytale.setScoreCount(scoreCount);

        return new FairyTaleResponseDto.FairyTaleDto(findFairytale.getFairytaleId(), findFairytale.getTitle(), findFairytale.getAvgScore());
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

        List<String> genres = List.of("한국 전래 동화", "세계 전래 동화", "판타지 동화", "동물 동화", "가족 동화", "의사 직업 동화", "소방관 직업 동화", "경찰 직업 동화", "요리사 직업 동화", "선생님 직업 동화", "과학자 직업 동화", "우주인 직업 동화", "운동선수 직업 동화", "수의사 직업 동화", "예술가 직업 동화");

        long jobFtCounts = 0;

        List<FairyTaleResponseDto.CountByGenre> countByGenres = new ArrayList<>();

        for (int i=0; i<genres.size(); i++){
            if (i<5){
                long count = fairyTaleRepository.countByGenre(genres.get(i));
                countByGenres.add(new FairyTaleResponseDto.CountByGenre(genres.get(i), count));
            } else {
                jobFtCounts = jobFtCounts + fairyTaleRepository.countByGenre(genres.get(i));
            }

            if (i==genres.size()-1){
                countByGenres.add(new FairyTaleResponseDto.CountByGenre("직업 동화", jobFtCounts));
            }

        }

//        List<FairyTaleResponseDto.CountByGenre> countByGenres = genres.stream().map(g -> {
//            long count = fairyTaleRepository.countByGenre(g);
//            return new FairyTaleResponseDto.CountByGenre(g, count);
//        }).collect(Collectors.toList());


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
