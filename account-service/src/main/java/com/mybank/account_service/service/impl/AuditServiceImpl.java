package com.mybank.account_service.service.impl;

import com.mybank.account_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    // REQUIRES_NEW — həmişə ayrı transaction
    // Əsas transaction rollback olsa belə audit qalır!
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long accountId,
                    String action,
                    String details) {
        try {
            log.info("[AUDIT] accountId={}, action={}, details={}",
                    accountId, action, details);
            //audit-ms-client cagrilacaq(db-e yazilmasi ucun)

        } catch (Exception e) {
            log.error("[AUDIT] Xəta: accountId={}, action={}, error={}",
                    accountId, action, e.getMessage());
        }
    }
}
