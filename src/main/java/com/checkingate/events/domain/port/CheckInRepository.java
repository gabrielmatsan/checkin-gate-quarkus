package com.checkingate.events.domain.port;

import java.util.List;
import java.util.Optional;

import com.checkingate.events.domain.entity.CheckIn;

public interface CheckInRepository {
    CheckIn save(CheckIn checkIn);
    Optional<CheckIn> findByUserAndActivity(String userId, String activityId);
    List<CheckIn> findByActivityIds(List<String> activityIds);
    List<CheckIn> findByUserId(String userId);
}
