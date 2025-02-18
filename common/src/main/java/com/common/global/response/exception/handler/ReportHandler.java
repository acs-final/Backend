package com.common.global.response.exception.handler;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.exception.GeneralException;

public class ReportHandler extends GeneralException {
    public ReportHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}