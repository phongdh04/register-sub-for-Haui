package com.example.demo.service.impl;

import com.example.demo.domain.entity.NotificationQueue;
import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.domain.enums.NotificationQueueStatus;
import com.example.demo.repository.NotificationQueueRepository;
import com.example.demo.service.INotificationQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationQueueServiceImpl implements INotificationQueueService {

    private final NotificationQueueRepository notificationQueueRepository;

    @Override
    @Transactional
    public void enqueueScheduleChanged(ScheduleChangeSet changeSet, List<Long> affectedSvIds) {
        if (changeSet == null) {
            return;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("event", "SCHEDULE_CHANGED");
        payload.put("changeSetId", changeSet.getId());
        payload.put("hocKyId", changeSet.getHocKy() != null ? changeSet.getHocKy().getIdHocKy() : null);
        payload.put("affectedSvCount", affectedSvIds != null ? affectedSvIds.size() : 0);
        payload.put("affectedSvIds", affectedSvIds != null ? affectedSvIds : List.of());
        payload.put("delta", changeSet.getPayloadDelta());

        NotificationQueue row = NotificationQueue.builder()
                .eventType("SCHEDULE_CHANGED")
                .hocKy(changeSet.getHocKy())
                .changeSet(changeSet)
                .payload(payload)
                .trangThai(NotificationQueueStatus.PENDING)
                .build();
        notificationQueueRepository.save(row);
        log.atInfo().log(
                "notification_queue enqueue event=SCHEDULE_CHANGED changeSetId={} hocKyId={} affectedSvCount={}",
                changeSet.getId(),
                changeSet.getHocKy() != null ? changeSet.getHocKy().getIdHocKy() : null,
                affectedSvIds != null ? affectedSvIds.size() : 0);
    }
}
