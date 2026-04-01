package com.mybank.account_service.controller.v1;

import com.mybank.account_service.dto.request.PlaceHoldRequest;
import com.mybank.account_service.dto.response.HoldResponse;
import com.mybank.account_service.service.HoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holds")
@RequiredArgsConstructor
public class HoldController {

    private final HoldService holdService;
    @PostMapping
    public ResponseEntity<HoldResponse> placeHold(@Valid @RequestBody PlaceHoldRequest request) {
        HoldResponse response = holdService.placeHold(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{holdId}/release")
    public ResponseEntity<HoldResponse> releaseHold(@PathVariable Long holdId) {
        return ResponseEntity.ok(holdService.releaseHold(holdId));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<HoldResponse>> getActiveHolds(@PathVariable Long accountId) {
        return ResponseEntity.ok(holdService.getActiveHolds(accountId));
    }
}