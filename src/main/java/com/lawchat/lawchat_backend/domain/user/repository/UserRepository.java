package com.lawchat.lawchat_backend.domain.user.repository;

import com.lawchat.lawchat_backend.domain.user.entity.User;
import com.lawchat.lawchat_backend.global.oauth2.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findBySocialProviderAndSocialId(OAuth2Provider socialProvider, String socialId);
}
