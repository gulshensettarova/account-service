package com.mybank.account_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceEntity {

    @Id
    @Column(name = "account_id")
    private Long accountId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id")
    private AccountEntity account;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "blocked_balance", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal blockedBalance = BigDecimal.ZERO;

    @Version
    private Long version;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}