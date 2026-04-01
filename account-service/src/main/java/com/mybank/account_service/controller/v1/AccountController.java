package com.mybank.account_service.controller.v1;

import com.mybank.account_service.dto.request.CreateAccountRequest;
import com.mybank.account_service.dto.response.AccountResponse;
import com.mybank.account_service.service.AccountService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Hesab idarəetmə əməliyyatları")
public class AccountController {

    private final AccountService accountService;
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Hesab ID", required = true) @PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getCustomerAccounts(
            @Parameter(description = "Müştəri ID", required = true) @PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getCustomerAccounts(customerId));
    }

    @PatchMapping("/{accountId}/block")
    public ResponseEntity<Void> blockAccount(
            @Parameter(description = "Hesab ID", required = true) @PathVariable Long accountId) {
        accountService.blockAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{accountId}/activate")
    public ResponseEntity<Void> activateAccount(
            @Parameter(description = "Hesab ID", required = true) @PathVariable Long accountId) {
        accountService.activateAccount(accountId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{accountId}/close")
    public ResponseEntity<Void> closeAccount(
            @Parameter(description = "Hesab ID", required = true) @PathVariable Long accountId) {
        accountService.closeAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}