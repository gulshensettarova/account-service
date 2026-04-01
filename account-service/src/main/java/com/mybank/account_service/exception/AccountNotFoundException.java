package com.mybank.account_service.exception;

public class AccountNotFoundException extends BankException {

    public AccountNotFoundException(Long accountId) {
        super("ACCOUNT_NOT_FOUND", "Hesab tapılmadı: " + accountId, 404);
    }

    public AccountNotFoundException(String accountNumber) {
        super("ACCOUNT_NOT_FOUND", "Hesab tapılmadı: " + accountNumber, 404);
    }
}