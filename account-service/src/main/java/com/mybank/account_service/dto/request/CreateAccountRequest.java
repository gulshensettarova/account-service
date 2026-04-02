package com.mybank.account_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "Müştəri ID boş ola bilməz")
    private Long customerId;

    @NotBlank(message = "Valyuta boş ola bilməz")
    @Size(min = 3, max = 3, message = "Valyuta 3 simvol olmalıdır")
    private String currency;

    @DecimalMin(value = "0.0", message = "Başlanğıc balans mənfi ola bilməz")
    private java.math.BigDecimal initialBalance;
}