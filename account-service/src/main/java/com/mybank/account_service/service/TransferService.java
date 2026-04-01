package com.mybank.account_service.service;

import com.mybank.account_service.dto.request.TransferRequest;
import com.mybank.account_service.dto.response.TransferResponse;

public interface TransferService {
    TransferResponse transfer(TransferRequest request);
}