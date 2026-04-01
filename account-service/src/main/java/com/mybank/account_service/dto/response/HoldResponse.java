package com.mybank.account_service.dto.response;

import com.mybank.account_service.enums.HoldStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class HoldResponse {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String reason;
    private HoldStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime releasedAt;
}