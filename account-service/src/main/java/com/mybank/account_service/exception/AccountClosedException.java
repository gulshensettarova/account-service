package com.mybank.account_service.exception;

public class AccountClosedException extends BankException {

    public AccountClosedException(Long accountId) {
        super("ACCOUNT_CLOSED", "Hesab bağlanıb: " + accountId, 422);
    }
}