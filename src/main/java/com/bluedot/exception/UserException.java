package com.bluedot.exception;

/**
 * @author FireRain
 * @version 1.0
 * @date 2022/8/17 11:36
 * @created: 自定义异常类
 */
public class UserException extends RuntimeException {

    /**
     * 附加自定义的错误信息到异常中
     */
    private ErrorCode errorCode;

    //ErrorCode属性的get&set方法

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    //五种本异常类的构造方法

    public UserException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public UserException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public UserException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public UserException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public UserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}


