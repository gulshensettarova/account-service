package com.mybank.account_service.repository;

import com.mybank.account_service.entity.AccountHoldEntity;
import com.mybank.account_service.enums.HoldStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountHoldRepository extends JpaRepository<AccountHoldEntity, Long> {

    // ─── Aktiv hold-lar ─────────────────────────────────────

    List<AccountHoldEntity> findByAccountIdAndStatus(Long accountId, HoldStatus status);

    // Hold tapılsın — yalnız ACTIVE olanlar
    @Query("""
        SELECT h FROM AccountHoldEntity h
        WHERE h.id = :id
        AND h.status = 'ACTIVE'
    """)
    Optional<AccountHoldEntity> findActiveById(
            @Param("id") Long id
    );

    // ─── Vaxtı keçmiş hold-lar ──────────────────────────────

    // Scheduler üçün — vaxtı keçmiş hold-ları tap
    @Query("""
        SELECT h FROM AccountHoldEntity h
        WHERE h.status = 'ACTIVE'
        AND h.expiresAt IS NOT NULL
        AND h.expiresAt < :now
    """)
    List<AccountHoldEntity> findExpiredHolds(
            @Param("now") LocalDateTime now
    );

    // ─── Bulk UPDATE ─────────────────────────────────────────

    // Vaxtı keçmiş hold-ları RELEASED et
    @Modifying
    @Query("""
        UPDATE AccountHoldEntity h
        SET h.status     = 'RELEASED',
            h.releasedAt = :now
        WHERE h.status = 'ACTIVE'
        AND h.expiresAt IS NOT NULL
        AND h.expiresAt < :now
    """)
    int releaseExpiredHolds(@Param("now") LocalDateTime now);

    // Hesabın ümumi aktiv hold məbləği
    @Query("""
        SELECT COALESCE(SUM(h.amount), 0)
        FROM AccountHoldEntity h
        WHERE h.accountId = :accountId
        AND h.status = 'ACTIVE'
    """)
    java.math.BigDecimal sumActiveHolds(
            @Param("accountId") Long accountId
    );
}