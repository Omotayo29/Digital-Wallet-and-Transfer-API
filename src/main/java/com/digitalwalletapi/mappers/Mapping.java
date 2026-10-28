package com.digitalwalletapi.mappers;

import com.digitalwalletapi.dto.request.CreateUserRequest;
import com.digitalwalletapi.dto.response.TransactionResponse;
import com.digitalwalletapi.dto.response.UserResponse;
import com.digitalwalletapi.dto.response.WalletResponse;
import com.digitalwalletapi.model.Transaction;
import com.digitalwalletapi.model.User;
import com.digitalwalletapi.model.Wallet;
import org.springframework.stereotype.Component;

@Component
public class Mapping {

    public User createUserRequestDTOtoUser(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        return user;
    }

    public TransactionResponse transactiontoTransactionResponseDTO(Transaction transaction) {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setReference(transaction.getReference());
        transactionResponse.setAmount(transaction.getAmount());
        transactionResponse.setTransactionType(transaction.getType());
        transactionResponse.setTransactionStatus(transaction.getStatus());
        transactionResponse.setCreatedAt(transaction.getCreatedAt());
        transactionResponse.setSenderUserId( transaction.getSenderWallet() == null
                ? null
                : transaction.getSenderWallet().getUser().getId());
        transactionResponse.setReceiverUserId(transaction.getReceiverWallet().getUser().getId());
        return transactionResponse;
    }

    public UserResponse usertoUserResponseDTO(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setId(user.getId());
        return userResponse;
    }

    public WalletResponse wallettoWalletResponseDTO(Wallet wallet) {
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setBalance(wallet.getBalance());
        walletResponse.setCurrency(wallet.getCurrency());
        walletResponse.setUserId(wallet.getUser().getId());
        return walletResponse;
    }

}
