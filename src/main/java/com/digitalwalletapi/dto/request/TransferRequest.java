package com.digitalwalletapi.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferRequest {

    @NotNull(message = "Sender user ID is required")
    private UUID senderUserId;

    @NotNull
    private UUID receiverUserId;

    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;
}
