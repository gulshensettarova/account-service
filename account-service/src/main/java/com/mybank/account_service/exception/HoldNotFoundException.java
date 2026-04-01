package com.mybank.account_service.exception;

public class HoldNotFoundException extends BankException {

    public HoldNotFoundException(Long holdId) {
        super("HOLD_NOT_FOUND", "Hold tapılmadı: " + holdId, 404);
    }
}