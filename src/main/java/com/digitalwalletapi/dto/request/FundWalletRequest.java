package com.digitalwalletapi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class FundWalletRequest {

    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
