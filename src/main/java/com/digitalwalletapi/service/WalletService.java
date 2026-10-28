package com.digitalwalletapi.service;

import com.digitalwalletapi.dto.request.FundWalletRequest;
import com.digitalwalletapi.dto.response.TransactionResponse;
import com.digitalwalletapi.dto.response.WalletResponse;
import com.digitalwalletapi.exception.InvalidTransferException;
import com.digitalwalletapi.exception.ResourceNotFoundException;
import com.digitalwalletapi.mappers.Mapping;
import com.digitalwalletapi.model.Transaction;
import com.digitalwalletapi.model.TransactionStatus;
import com.digitalwalletapi.model.TransactionType;
import com.digitalwalletapi.model.Wallet;
import com.digitalwalletapi.repository.TransactionRepository;
import com.digitalwalletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    @Autowired
    private Mapping mapping;

    public WalletResponse getWalletById(UUID id) {
        return mapping.wallettoWalletResponseDTO(walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found")));
    }

    public WalletResponse getWalletByUserId(UUID userId) {
        return mapping.wallettoWalletResponseDTO(
                walletRepository.findByUser_Id(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Wallet not found"))
        );
    }

    @Transactional
    public TransactionResponse fundWallet(UUID walletId, FundWalletRequest fundWalletRequest) {
        validatePositiveAmount(fundWalletRequest.getAmount());

        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(fundWalletRequest.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setIdempotencyKey(null);
        transaction.setType(TransactionType.FUNDING);
        transaction.setReceiverWallet(wallet);
        transaction.setAmount(fundWalletRequest.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);

        walletRepository.save(wallet);
        transactionRepository.save(transaction);
        return mapping.transactiontoTransactionResponseDTO(transaction);
    }

    public Page<TransactionResponse> getTransactionHistory(UUID walletId, Pageable pageable) {
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        Page<Transaction> transactions =
                transactionRepository.findByReceiverWalletOrSenderWalletOrderByCreatedAtDesc(
                wallet, wallet, pageable);
        return transactions.map(mapping::transactiontoTransactionResponseDTO);
    }

    private void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Amount must be positive");
        }
    }
}
