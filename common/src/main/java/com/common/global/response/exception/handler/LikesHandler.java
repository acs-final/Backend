package com.common.global.response.exception.handler;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.exception.GeneralException;

public class LikesHandler extends GeneralException {
    public LikesHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}