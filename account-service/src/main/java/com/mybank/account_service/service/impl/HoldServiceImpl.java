package com.mybank.account_service.service.impl;

import com.mybank.account_service.dto.request.PlaceHoldRequest;
import com.mybank.account_service.dto.response.HoldResponse;
import com.mybank.account_service.entity.AccountHoldEntity;
import com.mybank.account_service.enums.AccountStatus;
import com.mybank.account_service.enums.HoldStatus;
import com.mybank.account_service.exception.*;
import com.mybank.account_service.mapper.AccountMapper;
import com.mybank.account_service.repository.*;
import com.mybank.account_service.service.AuditService;
import com.mybank.account_service.service.HoldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoldServiceImpl implements HoldService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository balanceRepository;
    private final AccountHoldRepository holdRepository;
    private final AuditService auditService;
    private final AccountMapper accountMapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public HoldResponse placeHold(PlaceHoldRequest request) {
        log.info("Hold qoyulur: accountId={}, amount={}",
                request.getAccountId(), request.getAmount());

        // Pessimistic Lock
        var account = accountRepository
                .findByIdWithLock(request.getAccountId())
                .orElseThrow(() ->
                        new AccountNotFoundException(request.getAccountId()));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new AccountBlockedException(request.getAccountId());
        }
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountClosedException(request.getAccountId());
        }

        // Atomic UPDATE — yoxla + yenilə
        int updated = balanceRepository.placeHold(request.getAccountId(), request.getAmount());

        if (updated == 0) {
            throw new InsufficientFundsException(
                    account.getBalance().getAvailableBalance(),
                    request.getAmount()
            );
        }

        AccountHoldEntity hold = accountMapper.toHoldEntity(request);
        hold.setStatus(HoldStatus.ACTIVE);
        AccountHoldEntity saved = holdRepository.save(hold);

        auditService.log(request.getAccountId(),
                "HOLD_PLACED", "Hold qoyuldu: " + request.getAmount() + " AZN, Səbəb: " + request.getReason());
        log.info("Hold qoyuldu: holdId={}, accountId={}, amount={}",
                saved.getId(), request.getAccountId(), request.getAmount());
        return accountMapper.toHoldResponse(saved);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public HoldResponse releaseHold(Long holdId) {
        log.info("Hold açılır: holdId={}", holdId);
        AccountHoldEntity hold = holdRepository
                .findActiveById(holdId)
                .orElseThrow(() -> new HoldNotFoundException(holdId));
        int updated = balanceRepository.releaseHold(hold.getAccountId(), hold.getAmount());
        if (updated == 0) {
            throw new BankException("HOLD_RELEASE_FAILED", "Hold açılarkən xəta baş verdi", 500);
        }
        hold.setStatus(HoldStatus.RELEASED);
        hold.setReleasedAt(LocalDateTime.now());
        holdRepository.save(hold);
        auditService.log(hold.getAccountId(), "HOLD_RELEASED", "Hold açıldı: " + hold.getAmount() + " AZN");
        log.info("Hold açıldı: holdId={}, accountId={}, amount={}",
                holdId, hold.getAccountId(), hold.getAmount());
        return accountMapper.toHoldResponse(hold);
    }

    // ─── Aktiv hold-lar ─────────────────────────────────────

    @Override
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<HoldResponse> getActiveHolds(Long accountId) {
        return accountMapper.toHoldResponseList(holdRepository.findByAccountIdAndStatus(accountId, HoldStatus.ACTIVE));
    }

    // ─── Vaxtı keçmiş hold-lar ──────────────────────────────
    @Override
    @Scheduled(fixedDelay = 60_000)
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void releaseExpiredHolds() {
        LocalDateTime now = LocalDateTime.now();
        List<AccountHoldEntity> expired = holdRepository.findExpiredHolds(now);
        if (expired.isEmpty()) return;
        log.info("Vaxtı keçmiş {} hold tapıldı", expired.size());
        for (AccountHoldEntity hold : expired) {
            try {
                releaseHold(hold.getId());
            } catch (Exception e) {
                log.error("Hold açılarkən xəta: holdId={}, error={}", hold.getId(), e.getMessage());
            }
        }
    }
}