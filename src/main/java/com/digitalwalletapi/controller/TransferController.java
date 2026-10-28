package com.digitalwalletapi.controller;

import com.digitalwalletapi.dto.request.TransferRequest;
import com.digitalwalletapi.dto.response.TransactionResponse;
import com.digitalwalletapi.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("transfer")
public class TransferController {
    @Autowired
    private TransferService transferService;

    @PostMapping("createTransfer")
    public ResponseEntity<TransactionResponse>  createTransfer(@Valid @RequestBody TransferRequest transferRequest, @RequestHeader ("Idempotency-key") String idempotencyKey) {
        return ResponseEntity.ok().body(transferService.transfer(transferRequest, idempotencyKey));
    }
}
