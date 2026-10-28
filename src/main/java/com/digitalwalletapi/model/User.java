package com.digitalwalletapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank
    private String fullName;

    @Column(unique = true)
    @Email
    @NotBlank
    private String email;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PreUpdate
    private void normalizeEmail() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }
    }

    @PrePersist
    private void initialize() {
        normalizeEmail();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "user")
    private Wallet wallet;
}
