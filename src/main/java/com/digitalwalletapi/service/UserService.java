package com.digitalwalletapi.service;

import com.digitalwalletapi.dto.request.CreateUserRequest;
import com.digitalwalletapi.dto.response.UserResponse;
import com.digitalwalletapi.exception.DuplicateEmailException;
import com.digitalwalletapi.exception.ResourceNotFoundException;
import com.digitalwalletapi.mappers.Mapping;
import com.digitalwalletapi.model.User;
import com.digitalwalletapi.model.Wallet;
import com.digitalwalletapi.repository.UserRepository;
import com.digitalwalletapi.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    @Autowired
    private Mapping mapping;

    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        String email = createUserRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already exists");
        }

        User savedUser = mapping.createUserRequestDTOtoUser(createUserRequest);
        savedUser = userRepository.saveAndFlush(savedUser);

        Wallet wallet = createWallet(savedUser);
        savedUser.setWallet(wallet);
        walletRepository.save(wallet);

        return mapping.usertoUserResponseDTO(savedUser);
    }

    public Wallet createWallet(User user){
        Wallet newWallet = new Wallet();
        newWallet.setBalance(BigDecimal.ZERO);
        newWallet.setUser(user);
        return newWallet;
    }

    public UserResponse getUserById(UUID id){
        return mapping.usertoUserResponseDTO(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }
}
