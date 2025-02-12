package acs.aws_final_project.domain.Report;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.member.Member;
import acs.aws_final_project.domain.Report.dto.ReportRequestDto;
import acs.aws_final_project.domain.Report.dto.ReportResponseDto;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.member.MemberRepository;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
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
                .orElseThrow(() -> new RuntimeException("해당 독후감을 찾을 수 없습니다."));
        return ReportResponseDto.ReportDetailDto.builder()
                .reportId(report.getReportId())
                .title(report.getTitle())
                .body(report.getBody())
                .score(report.getScore())
                .memberId(report.getMember().getMemberId())
                .fairytaleId(report.getFairyTale().getFairytaleId()) // 또는 .getId() 확인 필요
                .createdAt(report.getCreatedAt())
                .build();
    }

    // 독후감 생성
    @Transactional
    public ReportResponseDto.ReportCreateDto createReport(ReportRequestDto.ReportCreateDto createDto) {
        // memberRepository와 storyBookRepository를 통해 실제 회원과 스토리북 객체를 조회합니다.
        Member member = memberRepository.findById(createDto.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));
        Fairytale storyBook = storyBookRepository.findById(createDto.getFairytaleId())
                .orElseThrow(() -> new RuntimeException("해당 스토리북을 찾을 수 없습니다."));

        Report report = Report.builder()
                .title(createDto.getTitle())
                .body(createDto.getBody())
                .score(createDto.getScore())
                .member(member)
                .fairyTale(storyBook)
                .build();
        Report savedReport = reportRepository.save(report);
        return ReportResponseDto.ReportCreateDto.builder()
                .reportId(savedReport.getReportId())
                .build();
    }

    // 독후감 수정
    @Transactional
    public ReportResponseDto.ReportCreateDto updateReport(Long reportId, ReportRequestDto.ReportUpdateDto updateDto) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 독후감을 찾을 수 없습니다."));
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
    public Long deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("해당 독후감을 찾을 수 없습니다."));
        reportRepository.delete(report);
        return reportId;
    }

    // 전체 독후감 목록 조회 (클라이언트에 제목과 평점만 전달)
    public List<ReportResponseDto.ReportListDto> getReports() {
        List<Report> reports = reportRepository.findAll();
        return reports.stream()
                .map(report -> ReportResponseDto.ReportListDto.builder()
                        .title(report.getTitle())
                        .score(report.getScore())
                        .build())
                .collect(Collectors.toList());
    }
}
