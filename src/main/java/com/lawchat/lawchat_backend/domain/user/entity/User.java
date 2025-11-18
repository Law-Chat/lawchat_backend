package com.lawchat.lawchat_backend.domain.user.entity;

import com.lawchat.lawchat_backend.global.common.BaseTimeEntity;
import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuth2Provider socialProvider;  // OAuth2Provider enum 사용

    @Column(nullable = false)
    private String socialId;  // providerId -> socialId로 변경

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User update(String name) {
        this.name = name;
        return this;
    }
}