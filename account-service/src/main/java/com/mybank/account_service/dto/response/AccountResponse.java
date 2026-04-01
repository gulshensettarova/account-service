package com.mybank.account_service.dto.response;

import com.mybank.account_service.enums.AccountStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Builder
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String iban;
    private Long customerId;
    private String currency;
    private AccountStatus status;
    private BalanceResponse balance;
    private LocalDateTime createdAt;
}