package com.happiegolucky.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data // Lombok: Getters, Setters, toString, etc.
@NoArgsConstructor // Lombok: Required for JPA
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // This will store the ENCRYPTED password

    private String email;

    // For simple role-based authorization (we will use this later)
    private String role = "USER";
}
