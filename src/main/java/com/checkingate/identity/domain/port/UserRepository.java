package com.checkingate.identity.domain.port;

import java.util.List;
import java.util.Optional;

import com.checkingate.identity.domain.entity.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findByIds(List<String> ids);
    void update(User user);
    void delete(String id);
}
