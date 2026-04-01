package com.mybank.account_service.repository;

import com.mybank.account_service.entity.AccountBalanceEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalanceEntity, Long> {

    // ─── Pessimistic Lock — balans dəyişimi üçün ────────────

    // Köçürmə zamanı balansa lock qoy
    // Başqa transaction toxuna bilməz
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT b FROM AccountBalanceEntity b
        WHERE b.accountId = :accountId
    """)
    Optional<AccountBalanceEntity> findByAccountIdWithLock(
            @Param("accountId") Long accountId
    );

    // ─── Bulk UPDATE — performans üçün ──────────────────────

    // Available balansı yenilə
    @Modifying
    @Query("""
        UPDATE AccountBalanceEntity b
        SET b.availableBalance = b.availableBalance + :amount,
            b.updatedAt = CURRENT_TIMESTAMP
        WHERE b.accountId = :accountId
    """)
    int updateAvailableBalance(
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );

    // Hold qoyuldu — available azal, blocked artır
    @Modifying
    @Query("""
        UPDATE AccountBalanceEntity b
        SET b.availableBalance = b.availableBalance - :amount,
            b.blockedBalance   = b.blockedBalance   + :amount,
            b.updatedAt        = CURRENT_TIMESTAMP
        WHERE b.accountId = :accountId
        AND b.availableBalance >= :amount
    """)
    int placeHold(
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );

    // Hold açıldı — blocked azal, available artır
    @Modifying
    @Query("""
        UPDATE AccountBalanceEntity b
        SET b.availableBalance = b.availableBalance + :amount,
            b.blockedBalance   = b.blockedBalance   - :amount,
            b.updatedAt        = CURRENT_TIMESTAMP
        WHERE b.accountId = :accountId
        AND b.blockedBalance >= :amount
    """)
    int releaseHold(
            @Param("accountId") Long accountId,
            @Param("amount") BigDecimal amount
    );
}