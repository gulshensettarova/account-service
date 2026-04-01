package com.mybank.account_service.service.impl;

import com.mybank.account_service.dto.request.CreateAccountRequest;
import com.mybank.account_service.dto.response.AccountResponse;
import com.mybank.account_service.entity.*;
import com.mybank.account_service.enums.AccountStatus;
import com.mybank.account_service.exception.*;
import com.mybank.account_service.mapper.AccountMapper;
import com.mybank.account_service.repository.AccountRepository;
import com.mybank.account_service.service.AccountService;
import com.mybank.account_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AuditService auditService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Hesab yaradılır: customerId={}", request.getCustomerId());
        AccountEntity account = accountMapper.toEntity(request);
        account.setAccountNumber(generateAccountNumber());
        account.setIban(generateIban());
        account.setStatus(AccountStatus.ACTIVE);
        AccountBalanceEntity balance = accountMapper.toBalanceEntity(request);
        account.setBalance(balance);
        AccountEntity saved = accountRepository.save(account);
        auditService.log(saved.getId(), "ACCOUNT_CREATED", "Hesab yaradıldı: " + saved.getAccountNumber());
        log.info("Hesab yaradıldı: id={}, accountNumber={}", saved.getId(), saved.getAccountNumber());
        return accountMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public AccountResponse getAccount(Long accountId) {
        AccountEntity account = accountRepository
                .findByIdWithBalance(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));
        return accountMapper.toResponse(account);
    }


    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        List<AccountEntity> accounts = accountRepository
                .findByCustomerIdWithBalance(customerId);
        return accountMapper.toResponseList(accounts);
    }

    // ─── Hesab bloklamaq ────────────────────────────────────
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void blockAccount(Long accountId) {
        AccountEntity account = accountRepository
                .findByIdWithLock(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountBlockedException(accountId);
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException(accountId);
        }

        account.setStatus(AccountStatus.BLOCKED);
        auditService.log(accountId, "ACCOUNT_BLOCKED", "Hesab bloklandı");
        log.info("Hesab bloklandı: id={}", accountId);
    }

    // ─── Hesab aktivləşdirmək ───────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateAccount(Long accountId) {
        AccountEntity account = accountRepository
                .findByIdWithLock(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException(accountId);
        }
        if (account.getStatus() == AccountStatus.ACTIVE) {
            return;
        }
        account.setStatus(AccountStatus.ACTIVE);
        auditService.log(accountId, "ACCOUNT_ACTIVATED", "Hesab aktivləşdirildi");
        log.info("Hesab aktivləşdirildi: id={}", accountId);
    }

    // ─── Hesab bağlamaq ─────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeAccount(Long accountId) {

        AccountEntity account = accountRepository
                .findByIdWithBalance(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException(accountId);
        }

        BigDecimal available = account.getBalance()
                .getAvailableBalance();
        BigDecimal blocked = account.getBalance()
                .getBlockedBalance();

        if (available.compareTo(BigDecimal.ZERO) > 0
                || blocked.compareTo(BigDecimal.ZERO) > 0) {
            throw new BankException("ACCOUNT_HAS_BALANCE",
                    "Hesabda qalıq var, bağlamaq mümkün deyil. " + "Mövcud: " + available + ", Blok: " + blocked, 422);
        }

        account.setStatus(AccountStatus.CLOSED);
        auditService.log(accountId, "ACCOUNT_CLOSED", "Hesab bağlandı");
        log.info("Hesab bağlandı: id={}", accountId);
    }

    // ─── Status yoxlama ─────────────────────────────────────

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void validateAccountActive(Long accountId) {
        AccountEntity account = accountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountBlockedException(accountId);
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException(accountId);
        }
    }


    private String generateAccountNumber() {
        String number;
        do {
            number = "AZ" + String.format("%022d",
                    (long) (Math.random() * 1_000_000_000_000_000_000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private String generateIban() {
        return "AZ" + String.format("%02d", (int) (Math.random() * 100)) + "NABZ" +
                String.format("%020d", (long) (Math.random() * 1_000_000_000_000_000_000L));
    }
}