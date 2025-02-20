package com.common.global.response.exception.handler;

import com.common.global.response.code.BaseErrorCode;
import com.common.global.response.exception.GeneralException;

public class BookstoreHandler extends GeneralException {
    public BookstoreHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
