package com.mybank.account_service.dto.request.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateAccountRequest {
    @NotBlank
    @Size(max = 34)
    private String accountNumber;

    @NotBlank
    @Size(max = 34)
    private String iban;

    @NotNull
    private Long customerId;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

}