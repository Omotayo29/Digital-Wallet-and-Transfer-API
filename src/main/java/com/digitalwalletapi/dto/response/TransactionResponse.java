package com.digitalwalletapi.dto.response;

import com.digitalwalletapi.model.TransactionStatus;
import com.digitalwalletapi.model.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private String reference;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private LocalDateTime createdAt;
    private UUID senderUserId;
    private UUID receiverUserId;

}
