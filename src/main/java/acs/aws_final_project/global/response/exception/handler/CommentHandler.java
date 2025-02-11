package acs.aws_final_project.global.response.exception.handler;

import acs.aws_final_project.global.response.code.BaseErrorCode;
import acs.aws_final_project.global.response.exception.GeneralException;

public class CommentHandler extends GeneralException {
    public CommentHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}