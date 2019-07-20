package com.yiziton.dataweb.core.exception;

/**
 * 领域异常，用于抛出业务异常
 * @author xiaoHong
 */
public class DomainException extends RuntimeException {

    int errorCode;

    public DomainException(int errorCode, String message, Throwable e){
        super(message,e);
        this.errorCode = errorCode;
    }

    public DomainException(int errorCode, Throwable e){
        this(errorCode,null,e);
    }

    public DomainException(int errorCode, String message){
        this(errorCode,message,null);
    }

    public DomainException(int errorCode){
        this(errorCode,null,null);
    }

    public int getErrorCode() {
        return errorCode;
    }
}
