package com.mybank.account_service.entity;

import com.mybank.account_service.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "accounts", uniqueConstraints = {
                @UniqueConstraint(columnNames = "account_number"),
                @UniqueConstraint(columnNames = "iban")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 34, updatable = false)
    private String accountNumber;

    @Column(nullable = false, unique = true, length = 34, updatable = false)
    private String iban;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private AccountBalanceEntity balance;

    public void setBalance(AccountBalanceEntity balance) {
        this.balance = balance;
        balance.setAccount(this);
    }
}