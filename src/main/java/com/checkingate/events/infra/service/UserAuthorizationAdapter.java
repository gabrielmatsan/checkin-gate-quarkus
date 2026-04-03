package com.checkingate.events.infra.service;

import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.service.UserAuthorizationService;
import com.checkingate.identity.domain.entity.User;
import com.checkingate.identity.domain.port.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserAuthorizationAdapter implements UserAuthorizationService {

    @Inject
    UserRepository userRepository;

    @Override
    public Optional<UserInfo> getUserById(String userId) {
        return userRepository.findById(userId).map(this::toUserInfo);
    }

    @Override
    public boolean isUserAdmin(String userId) {
        return userRepository.findById(userId)
                .map(User::isAdmin)
                .orElse(false);
    }

    @Override
    public String getUserEmail(String userId) {
        return userRepository.findById(userId)
                .map(User::getEmail)
                .orElse("");
    }

    @Override
    public List<UserInfo> getUserInfoBatch(List<String> userIds) {
        return userRepository.findByIds(userIds).stream()
                .map(this::toUserInfo)
                .toList();
    }

    private UserInfo toUserInfo(User user) {
        return new UserInfo(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.isAdmin()
        );
    }
}
