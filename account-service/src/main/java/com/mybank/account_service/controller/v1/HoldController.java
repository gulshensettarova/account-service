package com.mybank.account_service.controller.v1;

import com.mybank.account_service.dto.request.PlaceHoldRequest;
import com.mybank.account_service.dto.response.HoldResponse;
import com.mybank.account_service.service.HoldService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holds")
@RequiredArgsConstructor
@Tag(name = "Hold", description = "Hesab bloklanma əməliyyatları")
public class HoldController {

    private final HoldService holdService;

    @PostMapping
    public ResponseEntity<HoldResponse> placeHold(
            @Valid @RequestBody PlaceHoldRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(holdService.placeHold(request));
    }

    @PatchMapping("/{holdId}/release")
    public ResponseEntity<HoldResponse> releaseHold(
            @Parameter(description = "Hold ID", required = true) @PathVariable Long holdId) {
        return ResponseEntity.ok(holdService.releaseHold(holdId));
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<HoldResponse>> getActiveHolds(
            @Parameter(description = "Hesab ID", required = true) @PathVariable Long accountId) {
        return ResponseEntity.ok(holdService.getActiveHolds(accountId));
    }
}