package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.Report.ReportRepository;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.image.ImageRepository;
import acs.aws_final_project.domain.member.dto.MemberResponseDto;
import acs.aws_final_project.global.response.code.resultCode.ErrorStatus;
import acs.aws_final_project.global.response.exception.handler.FairytaleHandler;
import acs.aws_final_project.global.response.exception.handler.MemberHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FairyTaleRepository fairyTaleRepository;
    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public MemberResponseDto.LoginResponseDto login(String memberId, String name){

        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findMember.isEmpty()){

            Member newMember = MemberConverter.toMember(memberId,name, 0);

            memberRepository.save(newMember);
            log.info("첫 로그인 성공: {}", newMember.getMemberId());
        }

        return new MemberResponseDto.LoginResponseDto(memberId, name);

    }

    public List<MemberResponseDto.MyFairytaleDto> getMyFairytale(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Fairytale> findFairytale = fairyTaleRepository.findAllByMember(findMember);


        List<MemberResponseDto.MyFairytaleDto> result = findFairytale.stream().map(ft -> {
            Image findImage = imageRepository.findFirstByFairytale(ft);
            Report findReport = reportRepository.findByFairyTale(ft);
            boolean hasReport = false;
            if (findReport != null) {
                hasReport = true;
            }
            return MemberConverter.toMyFairytale(findMember, ft, hasReport, findImage);
        }).toList();

        return result;
    }


}
