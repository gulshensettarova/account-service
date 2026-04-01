package com.mybank.account_service.service;

import com.mybank.account_service.dto.request.CreateAccountRequest;
import com.mybank.account_service.dto.response.AccountResponse;
import java.util.List;

public interface AccountService {

    AccountResponse createAccount(CreateAccountRequest request);
    AccountResponse getAccount(Long accountId);
    List<AccountResponse> getCustomerAccounts(Long customerId);
    void blockAccount(Long accountId);
    void activateAccount(Long accountId);
    void closeAccount(Long accountId);
    void validateAccountActive(Long accountId);
}