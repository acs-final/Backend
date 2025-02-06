package acs.aws_final_project.global.response.exception.handler;

import acs.aws_final_project.global.response.code.BaseErrorCode;
import acs.aws_final_project.global.response.exception.GeneralException;

public class SonnetHandler extends GeneralException {

    public SonnetHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
