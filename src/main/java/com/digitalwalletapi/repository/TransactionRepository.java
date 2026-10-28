package com.digitalwalletapi.repository;

import com.digitalwalletapi.model.Transaction;
import com.digitalwalletapi.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Transaction findByIdempotencyKey(String idempotencyKey);

    Page<Transaction> findByReceiverWalletOrSenderWalletOrderByCreatedAtDesc(Wallet senderWallet, Wallet receiverWallet, Pageable pageable);
}

