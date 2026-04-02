package com.mybank.account_service.repository;

import com.mybank.account_service.dto.response.AccountResponse;
import com.mybank.account_service.entity.AccountEntity;
import com.mybank.account_service.enums.AccountStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository
        extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByIban(String iban);

    List<AccountEntity> findByCustomerId(Long customerId);

    boolean existsByAccountNumber(String accountNumber);

    // ─── JOIN FETCH — N+1 həlli ─────────────────────────────

    @Query("""
        SELECT a FROM AccountEntity a
        JOIN FETCH a.balance
        WHERE a.id = :id
    """)
    Optional<AccountEntity> findByIdWithBalance(@Param("id") Long id);

    // Müştərinin bütün hesabları — balance ilə
    @Query("""
        SELECT a FROM AccountEntity a
        JOIN FETCH a.balance
        WHERE a.customerId = :customerId
        ORDER BY a.createdAt DESC
    """)
    List<AccountEntity> findByCustomerIdWithBalance(@Param("customerId") Long customerId);

    // Status üzrə — balance ilə
    @Query("""
        SELECT a FROM AccountEntity a
        JOIN FETCH a.balance
        WHERE a.status = :status
        ORDER BY a.createdAt DESC
    """)
    List<AccountEntity> findByStatusWithBalance(
            @Param("status") AccountStatus status
    );

    // ─── Pessimistic Lock — kritik əməliyyatlar ─────────────

    // Köçürmə, hold əməliyyatları üçün
    // SELECT ... FOR UPDATE — başqası toxuna bilməz
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM AccountEntity a
        JOIN FETCH a.balance
        WHERE a.id = :id
    """)
    Optional<AccountEntity> findByIdWithLock(
            @Param("id") Long id
    );

    // ─── Bulk əməliyyatlar ───────────────────────────────────

    // Status yenilə — Optimistic Lock bypass etmədən
    @Modifying
    @Query("""
        UPDATE AccountEntity a
        SET a.status = :status,
            a.updatedAt = CURRENT_TIMESTAMP
        WHERE a.id = :id
    """)
    int updateStatus(
            @Param("id") Long id,
            @Param("status") AccountStatus status
    );

    // ─── DTO Projection — yalnız lazım olan field-lər ────────

    @Query("""
        SELECT  AccountResponse(
            a.id,
            a.accountNumber,
            a.iban,
            a.customerId,
            a.currency,
            a.status,
            a.createdAt
        )
        FROM AccountEntity a
        WHERE a.customerId = :customerId
    """)
    List<AccountResponse> findSummaryByCustomerId(
            @Param("customerId") Long customerId
    );
}