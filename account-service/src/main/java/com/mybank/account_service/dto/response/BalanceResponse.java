package com.mybank.account_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class BalanceResponse {

    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;
    private BigDecimal totalBalance;     // available + blocked
    private LocalDateTime updatedAt;
}