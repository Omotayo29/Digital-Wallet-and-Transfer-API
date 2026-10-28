package com.digitalwalletapi.controller;

import com.digitalwalletapi.dto.request.FundWalletRequest;
import com.digitalwalletapi.dto.response.TransactionResponse;
import com.digitalwalletapi.dto.response.WalletResponse;
import com.digitalwalletapi.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("wallet")
public class WalletController {
    @Autowired
    private WalletService walletService;

    @GetMapping("getWallet/{walletId}")
    public ResponseEntity<WalletResponse> getWalletById(@PathVariable UUID walletId){
        return ResponseEntity.ok().body(walletService.getWalletById(walletId));
    }

    @PostMapping("fundWallet/{walletId}")
    public ResponseEntity<TransactionResponse> fundWallet(@PathVariable UUID walletId, @Valid @RequestBody FundWalletRequest fundWalletRequest){
        return ResponseEntity.ok().body(walletService.fundWallet(walletId,fundWalletRequest));
    }

    @GetMapping("transactionHistory/{walletId}")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(@PathVariable UUID walletId, Pageable pageable){
        return ResponseEntity.ok().body(walletService.getTransactionHistory(walletId,pageable));
    }
}
