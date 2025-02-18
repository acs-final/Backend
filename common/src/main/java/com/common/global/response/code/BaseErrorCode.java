package com.common.global.response.code;

public interface BaseErrorCode {
    public ErrorReasonDTO getReason();

    public ErrorReasonDTO getReasonHttpStatus();
}
