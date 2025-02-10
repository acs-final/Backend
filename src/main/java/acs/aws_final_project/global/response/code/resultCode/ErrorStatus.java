package acs.aws_final_project.global.response.code.resultCode;

import acs.aws_final_project.global.response.code.BaseErrorCode;
import acs.aws_final_project.global.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL401", "서버 오류"),

    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "NOVA404", "파일 업로드 실패"),
    FAIRYTALE_BADREQUEST(HttpStatus.BAD_REQUEST, "CLAUDE404","동화 생성 오류"),
    FAIRYTALE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAIRYTALE400", "동화를 찾을 수 없습니다."),

    BOOKSTORE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKSTORE400", "해당 게시글을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
