package acs.aws_final_project.global.response.exception.handler;

import acs.aws_final_project.global.response.code.BaseErrorCode;
import acs.aws_final_project.global.response.exception.GeneralException;

public class FairytaleHandler extends GeneralException {

    public FairytaleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
