// Base exception — bütün bank exception-ları bundan extend edir
package com.mybank.account_service.exception;

import lombok.Getter;

@Getter
public class BankException extends RuntimeException {

    private final String errorCode;
    private final int httpStatus;

    public BankException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}