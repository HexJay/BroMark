package com.jay.domain.activity.service;


import com.jay.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

/**
 * @author Jay
 * @date 2025/7/16 16:47
 * @description TODO
 */
@Service
public class RaffleActivityService extends AbstractRaffleActivity{
    public RaffleActivityService(IActivityRepository activityRepository) {
        super(activityRepository);
    }
}
