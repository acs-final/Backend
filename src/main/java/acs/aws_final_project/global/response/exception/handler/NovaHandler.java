package acs.aws_final_project.global.response.exception.handler;

import acs.aws_final_project.global.response.code.BaseErrorCode;
import acs.aws_final_project.global.response.exception.GeneralException;

public class NovaHandler extends GeneralException {

    public NovaHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
