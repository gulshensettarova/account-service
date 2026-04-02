package com.mybank.account_service.service.impl;

import com.mybank.account_service.dto.request.TransferRequest;
import com.mybank.account_service.dto.response.TransferResponse;
import com.mybank.account_service.enums.AccountStatus;
import com.mybank.account_service.exception.*;
import com.mybank.account_service.mapper.AccountMapper;
import com.mybank.account_service.repository.*;
import com.mybank.account_service.service.AuditService;
import com.mybank.account_service.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository balanceRepository;
    private final AuditService auditService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public TransferResponse transfer(TransferRequest request) {

        log.info("Köçürmə başladı: from={}, to={}, amount={}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        // Eyni hesaba köçürmə
        if (request.getFromAccountId().equals(request.getToAccountId())) {
            throw new BankException("SAME_ACCOUNT_TRANSFER", "Eyni hesaba köçürmə mümkün deyil", 422);
        }

        // Deadlock həlli — kiçik ID əvvəl
        Long firstId = Math.min(request.getFromAccountId(), request.getToAccountId());
        Long secondId = Math.max(request.getFromAccountId(), request.getToAccountId());

        // Pessimistic Lock — hər iki hesab + balance
        var first = accountRepository
                .findByIdWithLock(firstId)
                .orElseThrow(() -> new AccountNotFoundException(firstId));
        log.info("first account: {}", first);

        var second = accountRepository
                .findByIdWithLock(secondId)
                .orElseThrow(() -> new AccountNotFoundException(secondId));
        log.info("second account: {}", second); // ← second idi, first deyil!

        var fromAccount = request.getFromAccountId().equals(firstId) ? first : second;
        var toAccount = request.getFromAccountId().equals(firstId) ? second : first;

        // Status yoxla
        validateStatus(fromAccount.getStatus(), request.getFromAccountId());
        validateStatus(toAccount.getStatus(), request.getToAccountId());

        // Balans yoxla
        BigDecimal available = fromAccount.getBalance().getAvailableBalance();
        if (available.compareTo(request.getAmount()) < 0) {
            throw new InsufficientFundsException(available, request.getAmount());
        }

        // Entity üzərindən dəyiş — dirty checking işləyir
        // @Modifying bypass problemi yoxdur
        fromAccount.getBalance().setAvailableBalance(
                available.subtract(request.getAmount())
        );

        toAccount.getBalance().setAvailableBalance(
                toAccount.getBalance().getAvailableBalance().add(request.getAmount())
        );

        // Transaction bitəndə dirty checking avtomatik UPDATE yazır
        // Əl ilə save() lazım deyil

        // Audit
        auditService.log(
                request.getFromAccountId(),
                "TRANSFER_COMPLETED",
                String.format("Köçürmə: %s → %s, Məbləğ: %s %s",
                        request.getFromAccountId(), request.getToAccountId(),
                        request.getAmount(), fromAccount.getCurrency())
        );

        log.info("Köçürmə tamamlandı: from={}, to={}, amount={}",
                request.getFromAccountId(), request.getToAccountId(), request.getAmount());

        return accountMapper.toTransferResponse(request);
    }

    private void validateStatus(AccountStatus status, Long accountId) {
        if (status == AccountStatus.BLOCKED) {
            throw new AccountBlockedException(accountId);
        }
        if (status == AccountStatus.CLOSED) {
            throw new AccountClosedException(accountId);
        }
    }
}
