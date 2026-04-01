package com.mybank.account_service.exception;

public class ConcurrentUpdateException extends BankException {

    public ConcurrentUpdateException(String entity) {
        super("CONCURRENT_UPDATE", entity + " məlumatları dəyişib. Yenidən cəhd edin.", 409);
    }
}