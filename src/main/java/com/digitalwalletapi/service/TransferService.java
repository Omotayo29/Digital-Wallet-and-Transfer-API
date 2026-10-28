package com.digitalwalletapi.service;

import com.digitalwalletapi.dto.request.TransferRequest;
import com.digitalwalletapi.dto.response.TransactionResponse;
import com.digitalwalletapi.mappers.Mapping;
import com.digitalwalletapi.model.Transaction;
import com.digitalwalletapi.exception.InvalidTransferException;
import com.digitalwalletapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final TransactionRepository transactionRepository;
    private final TransferTransactionService transferTransactionService;
    @Autowired
    private Mapping mapping;

    public TransactionResponse transfer(
            TransferRequest transferRequest,
            String idempotencyKey
    ) {
        validateIdempotencyKey(idempotencyKey);

        Transaction existing = transactionRepository
                .findByIdempotencyKey(idempotencyKey);
        if (existing != null) {
            return mapping.transactiontoTransactionResponseDTO(existing);
        }

        validatePositiveAmount(transferRequest.getAmount());

        try {
            return mapping.transactiontoTransactionResponseDTO(transferTransactionService.executeTransfer(
                    transferRequest,
                    idempotencyKey
            ));
        } catch (DataIntegrityViolationException exception) {
            Transaction racedTransaction =
                    transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (racedTransaction != null) {
                return mapping.transactiontoTransactionResponseDTO(racedTransaction);
            }
            throw exception;
        }
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be positive");
        }
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new InvalidTransferException("Idempotency key is required");
        }
    }
}
