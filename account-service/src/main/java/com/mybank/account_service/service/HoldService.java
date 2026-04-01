package com.mybank.account_service.service;

import com.mybank.account_service.dto.request.PlaceHoldRequest;
import com.mybank.account_service.dto.response.HoldResponse;
import java.util.List;

public interface HoldService {
    HoldResponse placeHold(PlaceHoldRequest request);
    HoldResponse releaseHold(Long holdId);
    List<HoldResponse> getActiveHolds(Long accountId);
    void releaseExpiredHolds();
}