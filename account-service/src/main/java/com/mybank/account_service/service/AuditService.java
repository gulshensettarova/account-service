package com.mybank.account_service.service;

public interface AuditService {
    void log(Long accountId, String action, String details);
}