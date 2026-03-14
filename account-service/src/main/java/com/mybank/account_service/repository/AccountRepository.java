package com.mybank.account_service.repository;

import com.mybank.account_service.entity.AccountEntity;
import com.mybank.account_service.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    Optional<AccountEntity> findByIban(String iban);

    List<AccountEntity> findByCustomerId(Long customerId);

    List<AccountEntity> findByStatus(AccountStatus status);

}