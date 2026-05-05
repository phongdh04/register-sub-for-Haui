package com.example.demo.service;

import com.example.demo.domain.entity.ScheduleChangeSet;

import java.util.List;

/** BACK-TKB-031 — enqueue stub contract SCHEDULE_CHANGED. */
public interface INotificationQueueService {

    void enqueueScheduleChanged(ScheduleChangeSet changeSet, List<Long> affectedSvIds);
}
