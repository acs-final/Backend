package fairytale.service;

import com.common.global.response.code.resultCode.ErrorStatus;
import com.common.global.response.exception.handler.NovaHandler;
import fairytale.dto.fairyTale.FairyTaleRequestDto;

import fairytale.util.AmazonS3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.*;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PollyService {


    private final AmazonS3UploadService amazonS3UploadService;
    private final PollyClient pollyClient;


    //public String createMP3(String text, String fileDir, String fileName){
    public String createMP3(FairyTaleRequestDto.PollyRequestDto requestDto){

        log.info("text: {}", requestDto.getText());

        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(requestDto.getText())
                .voiceId(VoiceId.SEOYEON)  // 영어 여성 목소리
                .outputFormat(OutputFormat.MP3)
                .languageCode(LanguageCode.KO_KR)
                //.textType(TextType.SSML)  // SSML 태그 적용. 태그 없는 형식일 때 주석 처리.
                .build();

        String mp3Name = "";

        try (ResponseInputStream<SynthesizeSpeechResponse> response = pollyClient.synthesizeSpeech(request);
             InputStream audioStream = response) {

            /* 로컬 설치 */
//            try (FileOutputStream outputStream = new FileOutputStream(mp3Name)) {
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = audioStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//                System.out.println("MP3 파일 생성 완료: " + mp3Name);
//            }
            String fileName = requestDto.getTitle() + "/" + requestDto.getFileName() + ".mp3";

            mp3Name = uploadMP3(audioStream, fileName);

            return mp3Name;

        } catch (Exception e) {
            e.printStackTrace();
        }


        return mp3Name;

    }


    @Transactional
    public String uploadMP3(InputStream audioStream, String fileName) {

        String mp3Url;

        try {
            mp3Url = amazonS3UploadService.uploadMP3(audioStream, fileName, "polly-mp3");

        } catch (Exception e) {
            throw new NovaHandler(ErrorStatus.FILE_UPLOAD_FAILED);
        }

        return mp3Url;
    }

}
