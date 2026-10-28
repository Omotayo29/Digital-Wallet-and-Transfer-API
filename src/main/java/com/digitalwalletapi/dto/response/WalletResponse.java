package com.digitalwalletapi.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletResponse {
    private UUID userId;
    private BigDecimal balance;
    private String currency;
}
