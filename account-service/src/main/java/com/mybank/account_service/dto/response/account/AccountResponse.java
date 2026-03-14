package com.mybank.account_service.dto.response.account;

import java.time.LocalDateTime;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String iban;
    private Long customerId;
    private String currency;
    private String status;
    private LocalDateTime createdAt;

}