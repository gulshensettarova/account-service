package com.mybank.account_service.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_balances")
public class AccountBalanceEntity {
    @Id
    @Column(name = "account_id")
    private Long accountId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(name = "available_balance")
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "blocked_balance")
    private BigDecimal blockedBalance = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
