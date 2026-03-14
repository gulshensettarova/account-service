package com.mybank.account_service.dto.response.accounthold;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountHoldResponse {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

}