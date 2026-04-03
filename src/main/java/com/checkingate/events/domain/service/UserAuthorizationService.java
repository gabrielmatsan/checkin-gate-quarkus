package com.checkingate.events.domain.service;

import java.util.List;
import java.util.Optional;

public interface UserAuthorizationService {

    Optional<UserInfo> getUserById(String userId);

    boolean isUserAdmin(String userId);

    String getUserEmail(String userId);

    List<UserInfo> getUserInfoBatch(List<String> userIds);

    record UserInfo(
        String id,
        String firstName,
        String lastName,
        String email,
        boolean isAdmin
    ) {}
}
