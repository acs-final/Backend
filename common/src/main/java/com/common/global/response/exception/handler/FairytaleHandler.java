package com.common.global.response.exception.handler;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.exception.GeneralException;

public class FairytaleHandler extends GeneralException {

    public FairytaleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
