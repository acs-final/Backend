package member;



import com.common.entity.*;
import com.common.global.config.RedisService;
import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.FairytaleHandler;
import com.common.global.response.exception.handler.MemberHandler;
import com.common.repository.*;
import member.dto.MemberRequestDto;
import member.dto.MemberResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final FairytaleRepository fairyTaleRepository;
    private final BookstoreRepository bookstoreRepository;
    private final ImageRepository imageRepository;
    private final ReportRepository reportRepository;

    private final RedisService redisService;

    @Transactional
    public MemberResponseDto.LoginResponseDto login(String memberId){

//        Optional<Member> findMember = memberRepository.findById(memberId);

        Member deletedMember = memberRepository.findInactiveMemberById(memberId).orElse(null);

        log.info("deletedMember: {}", deletedMember);

        String myColor = "null";

        if (deletedMember != null) {  // 회원 탈퇴 했던 멤버가 다시 로그인할 때 활성화 시킴.
            deletedMember.setLoginStatus();
        } else {
            Member findMember = memberRepository.findById(memberId).orElse(null);

            if (findMember == null){ // 최초 로그인한 사람만 디비에 저장.

                Member newMember = MemberConverter.toMember(memberId, 0);

                redisService.saveData("memberId", newMember.getMemberId());

                memberRepository.save(newMember);
                log.info("첫 로그인 성공: {}", newMember.getMemberId());
            } else {
                myColor = findMember.getColor();  // 로그인 기록 있는 경우 색상만 가지고 반환.

                findMember.setLastVisit(LocalDate.now());

                redisService.saveData("memberId", findMember.getMemberId());
                memberRepository.save(findMember);
            }

        }

        redisService.getData("memberId");

        return new MemberResponseDto.LoginResponseDto(memberId, myColor);

    }

    @Transactional
    public MemberResponseDto.MemberResultDto updateMyProfile(String memberId, MemberRequestDto.UpdateProfileDto updateDto){

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
        if (updateDto.getColor() != null){
            findMember.setColor(updateDto.getColor());
        }

        memberRepository.save(findMember);

        return new MemberResponseDto.MemberResultDto(findMember.getMemberId());
    }

    @Transactional
    public MemberResponseDto.MemberResultDto deleteMember(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        memberRepository.delete(findMember);

        return new MemberResponseDto.MemberResultDto(memberId);
    }

    public MemberResponseDto.MemberDetailDto getMemberDetail(String memberId){

        Member findMember = memberRepository.findById(memberId).orElseThrow(()-> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return new  MemberResponseDto.MemberDetailDto(findMember.getNickname(), findMember.getName(), findMember.getCredit(), findMember.getChildAge(), findMember.getColor());
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
