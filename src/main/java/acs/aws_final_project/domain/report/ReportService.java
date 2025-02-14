package acs.aws_final_project.domain.report;

import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.domain.report.dto.ReportRequestDto;
import acs.aws_final_project.domain.report.dto.ReportResponseDto;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.member.MemberRepository;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.FairytaleHandler;
import acs.aws_final_project.global.response.exception.handler.MemberHandler;
import acs.aws_final_project.global.response.exception.handler.ReportHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;
    private final FairyTaleRepository storyBookRepository;

    // 단일 독후감 조회 (상세 정보 반환)
    public ReportResponseDto.ReportDetailDto getReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportHandler(ErrorStatus.REPORT_NOT_FOUND));
        return ReportResponseDto.ReportDetailDto.builder()
                .reportId(report.getReportId())
                .title(report.getTitle())
                .body(report.getBody())
                .score(report.getScore())
                .memberId(report.getMember().getMemberId())
                .fairytaleId(report.getFairyTale().getFairytaleId()) // 또는 .getId() 확인 필요
                .createdAt(report.getCreatedAt())
                .imageUrl(report.getImageUrl())
                .build();
    }

    // 독후감 생성
    @Transactional
    public ReportResponseDto.ReportCreateDto createReport(String memberId, ReportRequestDto.ReportCreateDto createDto) {
        // memberRepository와 storyBookRepository를 통해 실제 회원과 스토리북 객체를 조회합니다.
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Fairytale storyBook = storyBookRepository.findById(createDto.getFairytaleId())
                .orElseThrow(() -> new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));

        Report findReport = reportRepository.findByFairyTale(storyBook);

        if (findReport!=null){
            throw new ReportHandler(ErrorStatus.REPORT_ALREADY_EXIST);
        }

        Report report = Report.builder()
                .title(createDto.getTitle())
                .body(createDto.getBody())
                .score(createDto.getScore())
                .member(member)
                .fairyTale(storyBook)
                .imageUrl(createDto.getImageUrl())
                .build();


        Report savedReport = reportRepository.save(report);

        Integer credit = member.getCredit() + 20;
        member.setCredit(credit);
        memberRepository.save(member);

        return ReportResponseDto.ReportCreateDto.builder()
                .reportId(savedReport.getReportId())
                .build();
    }

    // 독후감 수정
    @Transactional
    public ReportResponseDto.ReportCreateDto updateReport(String memberId, Long reportId, ReportRequestDto.ReportUpdateDto updateDto) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportHandler(ErrorStatus.REPORT_NOT_FOUND));

        if (member != report.getMember()){
            throw new MemberHandler(ErrorStatus.MEMBER_BAD_REQUEST);
        }

        if (updateDto.getTitle() != null) {
            report.setTitle(updateDto.getTitle());
        }
        if (updateDto.getBody() != null) {
            report.setBody(updateDto.getBody());
        }
        if (updateDto.getScore() != null) {
            report.setScore(updateDto.getScore());
        }

        Report updatedReport = reportRepository.save(report);

        return ReportResponseDto.ReportCreateDto.builder()
                .reportId(updatedReport.getReportId())
                .build();
    }

    // 독후감 삭제
    @Transactional
    public Long deleteReport(String memberId, Long reportId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ReportHandler(ErrorStatus.REPORT_NOT_FOUND));

        if (member != report.getMember()){
            throw new MemberHandler(ErrorStatus.MEMBER_BAD_REQUEST);
        }

        reportRepository.delete(report);
        return reportId;
    }

    // 전체 독후감 목록 조회 (클라이언트에 제목과 평점만 전달)
    public List<ReportResponseDto.ReportListDto> getReports() {

        List<Report> reports = reportRepository.findAll();

        return reports.stream()
                .map(report -> ReportResponseDto.ReportListDto.builder()
                        .reportId(report.getReportId())
                        .genre(report.getFairyTale().getGenre())
                        .writer(report.getMember().getName())
                        .createdAt(report.getCreatedAt().toLocalDate())
                        .fairytaleId(report.getFairyTale().getFairytaleId())
                        .title(report.getTitle())
                        .score(report.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}
