package com.mybank.account_service.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends BankException {

    public InsufficientFundsException(
            BigDecimal available,
            BigDecimal requested) {
        super("INSUFFICIENT_FUNDS",
                String.format("Balans kifayət deyil. Mövcud: %s, Tələb: %s", available, requested), 422);
    }
}