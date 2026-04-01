package com.mybank.account_service.exception;

public class AccountBlockedException extends BankException {

    public AccountBlockedException(Long accountId) {
        super("ACCOUNT_BLOCKED", "Hesab bloklanıb: " + accountId, 422);
    }
}