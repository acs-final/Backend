package com.common.global.response.exception.handler;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.exception.GeneralException;

public class NovaHandler extends GeneralException {

    public NovaHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
