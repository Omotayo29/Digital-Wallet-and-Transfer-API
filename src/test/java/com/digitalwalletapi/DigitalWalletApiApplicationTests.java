package com.digitalwalletapi;

import com.digitalwalletapi.dto.request.TransferRequest;
import com.digitalwalletapi.exception.InsufficientBalanceException;
import com.digitalwalletapi.exception.InvalidTransferException;
import com.digitalwalletapi.model.User;
import com.digitalwalletapi.model.Wallet;
import com.digitalwalletapi.repository.TransactionRepository;
import com.digitalwalletapi.repository.WalletRepository;
import com.digitalwalletapi.service.TransferTransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalWalletApiApplicationTests {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private TransferTransactionService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferTransactionService(
                walletRepository,
                transactionRepository
        );
    }

    @Test
    void rejectsTransferBetweenDifferentCurrencies() {
        Wallet sender = wallet("NGN", "100.00");
        Wallet receiver = wallet("USD", "20.00");
        stubWallets(sender, receiver);

        assertThrows(InvalidTransferException.class, () ->
                transferService.executeTransfer(
                        request(sender, receiver, "10.00"), "transfer-1"));

        verify(walletRepository, never()).save(sender);
        verify(walletRepository, never()).save(receiver);
    }

    @Test
    void rejectsTransferWhenBalanceIsInsufficient() {
        Wallet sender = wallet("NGN", "5.00");
        Wallet receiver = wallet("NGN", "20.00");
        stubWallets(sender, receiver);

        assertThrows(InsufficientBalanceException.class, () ->
                transferService.executeTransfer(
                        request(sender, receiver, "10.00"), "transfer-2"));

        verify(walletRepository, never()).save(sender);
        verify(walletRepository, never()).save(receiver);
    }

    @Test
    void rejectsTransferToTheSameUser() {
        Wallet sender = wallet("NGN", "100.00");
        Wallet receiver = wallet("NGN", "20.00");
        receiver.setUser(sender.getUser());
        stubWallets(sender, receiver);

        assertThrows(InvalidTransferException.class, () ->
                transferService.executeTransfer(
                        request(sender, receiver, "10.00"), "transfer-3"));
    }

    private void stubWallets(Wallet sender, Wallet receiver) {
        when(walletRepository.findByUser_Id(sender.getUser().getId()))
                .thenReturn(Optional.of(sender));
        when(walletRepository.findByUser_Id(receiver.getUser().getId()))
                .thenReturn(Optional.of(receiver));
    }

    private TransferRequest request(Wallet sender, Wallet receiver, String amount) {
        TransferRequest request = new TransferRequest();
        request.setSenderUserId(sender.getUser().getId());
        request.setReceiverUserId(receiver.getUser().getId());
        request.setAmount(new BigDecimal(amount));
        return request;
    }

    private Wallet wallet(String currency, String balance) {
        User user = new User();
        user.setId(UUID.randomUUID());

        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setBalance(new BigDecimal(balance));
        return wallet;
    }
}
