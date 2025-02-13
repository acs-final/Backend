package acs.aws_final_project.domain.member;

import acs.aws_final_project.domain.Report.Report;
import acs.aws_final_project.domain.Report.ReportRepository;
import acs.aws_final_project.domain.bookstore.Bookstore;
import acs.aws_final_project.domain.bookstore.BookstoreRepository;
import acs.aws_final_project.domain.fairyTale.FairyTaleRepository;
import acs.aws_final_project.domain.fairyTale.Fairytale;
import acs.aws_final_project.domain.image.Image;
import acs.aws_final_project.domain.image.ImageRepository;
import acs.aws_final_project.domain.member.dto.MemberRequestDto;
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
    private final BookstoreRepository bookstoreRepository;
    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;

    @Transactional
    public MemberResponseDto.LoginResponseDto login(String memberId){

//        Optional<Member> findMember = memberRepository.findById(memberId);

        Member deletedMember = memberRepository.findInactiveMemberById(memberId).orElse(null);

        log.info("deletedMember: {}", deletedMember);

        if (deletedMember != null) {
            deletedMember.setLoginStatus();
        } else {
            Optional<Member> findMember = memberRepository.findById(memberId);

            if (findMember.isEmpty()){

                Member newMember = MemberConverter.toMember(memberId, 0);

                memberRepository.save(newMember);
                log.info("첫 로그인 성공: {}", newMember.getMemberId());
            }
        }


        return new MemberResponseDto.LoginResponseDto(memberId);

    }

    @Transactional
    public MemberResponseDto.LoginResponseDto updateMyProfile(String memberId, MemberRequestDto.UpdateProfileDto updateDto){

        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        if (updateDto.getNickname() != null){
            findMember.setNickname(updateDto.getNickname());
        }
        if (updateDto.getUsername() != null){
            findMember.setName(updateDto.getUsername());
        }
        if (updateDto.getChildAge() != null){
            findMember.setChildAge(updateDto.getChildAge());
        }

        memberRepository.save(findMember);

        return new MemberResponseDto.LoginResponseDto(findMember.getMemberId());
    }

    @Transactional
    public MemberResponseDto.LoginResponseDto deleteMember(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        memberRepository.delete(findMember);

        return new MemberResponseDto.LoginResponseDto(memberId);
    }

    public MemberResponseDto.MemberDetailDto getMemberDetail(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return new  MemberResponseDto.MemberDetailDto(findMember.getNickname(), findMember.getName(), findMember.getCredit(), findMember.getChildAge());
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

    public List<MemberResponseDto.MyBookstoreDto> getMyBookstore(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Bookstore> findBookstore = bookstoreRepository.findAllByMember(findMember);

        List<MemberResponseDto.MyBookstoreDto> result = findBookstore.stream().map(bs -> {
            Fairytale findFairytale = fairyTaleRepository.findById(bs.getFairytale().getFairytaleId()).orElseThrow(()->new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));
            return MemberConverter.toMyBookstore(findMember, bs, findFairytale);
        }).toList();

        return result;
    }


    public List<MemberResponseDto.MyReportDto> getMyReport(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Report> findBookstore = reportRepository.findAllByMember(findMember);

        List<MemberResponseDto.MyReportDto> result = findBookstore.stream().map(r -> {
            Fairytale findFairytale = fairyTaleRepository.findById(r.getFairyTale().getFairytaleId()).orElseThrow(()->new FairytaleHandler(ErrorStatus.FAIRYTALE_NOT_FOUND));
            return MemberConverter.toMyReport(findMember, r, findFairytale);
        }).toList();

        return result;
    }

}
