package com.digitalwalletapi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private LocalDateTime createdAt;

}
