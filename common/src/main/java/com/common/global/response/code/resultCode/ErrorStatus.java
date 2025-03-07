package com.common.global.response.code.resultCode;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // Global
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL401", "서버 오류"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER400", "사용자를 찾을 수 없습니다."),
    MEMBER_BAD_REQUEST(HttpStatus.NOT_FOUND, "MEMBER404", "잘못된 요청입니다."),

    FILE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "NOVA404", "파일 업로드 실패"),
    FAIRYTALE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "CLAUDE404","동화 생성 오류"),
    FAIRYTALE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAIRYTALE400", "동화를 찾을 수 없습니다."),
    FAIRYTALE_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FAIRYTALE405", "동화 텍스트 파싱 에러."),

    BOOKSTORE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKSTORE400", "해당 게시글을 찾을 수 없습니다."),
    BOOKSTORE_ALREADY_EXIST(HttpStatus.BAD_REQUEST , "BOOKSTORE403", "이미 생성된 게시글이 존재합니다."),

    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT400", "해당 댓글을 찾을 수 없습니다."),

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT400", "해당 독후감을 찾을 수 없습니다."),
    REPORT_ALREADY_EXIST(HttpStatus.BAD_REQUEST , "REPORT403", "이미 생성된 독후감이 존재합니다."),

    LIKE_IS_PRESSED(HttpStatus.BAD_REQUEST , "LIKE403", "이미 좋아요가 눌러진 상태입니다."),
    LIKE_NOT_PRESSED(HttpStatus.BAD_REQUEST , "LIKE403", "좋아요가 눌러져 있지 않습니다.");


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
