package com.digitalwalletapi.service;

import com.digitalwalletapi.dto.request.TransferRequest;
import com.digitalwalletapi.exception.InsufficientBalanceException;
import com.digitalwalletapi.exception.InvalidTransferException;
import com.digitalwalletapi.exception.ResourceNotFoundException;
import com.digitalwalletapi.model.Transaction;
import com.digitalwalletapi.model.TransactionStatus;
import com.digitalwalletapi.model.TransactionType;
import com.digitalwalletapi.model.Wallet;
import com.digitalwalletapi.repository.TransactionRepository;
import com.digitalwalletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferTransactionService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction executeTransfer(
            TransferRequest transferRequest,
            String idempotencyKey
    ) {
        Wallet sender = findWallet(transferRequest.getSenderUserId(), "Sender wallet not found");
        Wallet receiver = findWallet(transferRequest.getReceiverUserId(), "Receiver wallet not found");

        if (sender.getUser().getId().equals(receiver.getUser().getId())) {
            throw new InvalidTransferException(
                    "Sender and receiver must be different users");
        }

        if (!Objects.equals(sender.getCurrency(), receiver.getCurrency())) {
            throw new InvalidTransferException(
                    "Sender and receiver wallets must use the same currency");
        }

        if (sender.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        sender.setBalance(sender.getBalance().subtract(transferRequest.getAmount()));
        receiver.setBalance(receiver.getBalance().add(transferRequest.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setReference(UUID.randomUUID().toString());
        transaction.setIdempotencyKey(idempotencyKey);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setSenderWallet(sender);
        transaction.setReceiverWallet(receiver);
        transaction.setAmount(transferRequest.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);

        walletRepository.save(sender);
        walletRepository.save(receiver);

        return transactionRepository.saveAndFlush(transaction);
    }

    private Wallet findWallet(UUID userId, String message) {
        return walletRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException(message));
    }
}
