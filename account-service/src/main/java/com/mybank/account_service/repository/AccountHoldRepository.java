package com.mybank.account_service.repository;

import com.mybank.account_service.entity.AccountHoldEntity;
import com.mybank.account_service.enums.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountHoldRepository extends JpaRepository<AccountHoldEntity, Long> {
    List<AccountHoldEntity> findByAccountId(Long accountId);
    List<AccountHoldEntity> findByAccountIdAndStatus(Long accountId, HoldStatus status);

}