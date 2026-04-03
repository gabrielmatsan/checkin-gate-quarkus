package com.checkingate.events.application.usecase;

import java.util.List;

import com.checkingate.events.domain.entity.CheckIn;
import com.checkingate.events.domain.port.CheckInRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ListUserCheckInsUseCase {

    @Inject
    CheckInRepository checkInRepository;

    public List<CheckIn> execute(String userId) {
        return checkInRepository.findByUserId(userId);
    }
}
