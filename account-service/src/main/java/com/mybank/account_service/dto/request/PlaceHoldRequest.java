package com.mybank.account_service.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PlaceHoldRequest {

    @NotNull(message = "Hesab ID boş ola bilməz")
    private Long accountId;

    @NotNull(message = "Məbləğ boş ola bilməz")
    @DecimalMin(
            value = "0.01",
            message = "Məbləğ 0-dan böyük olmalıdır"
    )
    private BigDecimal amount;

    @NotBlank(message = "Səbəb boş ola bilməz")
    @Size(max = 255)
    private String reason;

    private LocalDateTime expiresAt;
}