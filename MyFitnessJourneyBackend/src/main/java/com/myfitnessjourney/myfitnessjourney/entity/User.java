package com.myfitnessjourney.myfitnessjourney.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(name = "oauth2_provider")
    private String oauth2Provider;

    @Column(name = "oauth2_provider_id")
    private String oauth2ProviderId;

    @Column(name = "name")
    private String name;

    @Column(name = "picture_url")
    private String pictureUrl;
}

