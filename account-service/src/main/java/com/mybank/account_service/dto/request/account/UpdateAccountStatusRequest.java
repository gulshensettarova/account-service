package com.mybank.account_service.dto.request.account;

import jakarta.validation.constraints.NotBlank;

public class UpdateAccountStatusRequest {

    @NotBlank
    private String status;

}