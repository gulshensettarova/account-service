package com.mybank.account_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class TransferRequest {

    @NotNull(message = "Göndərən hesab boş ola bilməz")
    private Long fromAccountId;

    @NotNull(message = "Alan hesab boş ola bilməz")
    private Long toAccountId;

    @NotNull(message = "Məbləğ boş ola bilməz")
    @DecimalMin(
            value = "0.01",
            message = "Məbləğ 0-dan böyük olmalıdır"
    )
    private BigDecimal amount;

    @Size(max = 255)
    private String description;
}