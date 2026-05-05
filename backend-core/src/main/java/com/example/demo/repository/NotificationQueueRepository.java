package com.example.demo.repository;

import com.example.demo.domain.entity.NotificationQueue;
import com.example.demo.domain.enums.NotificationQueueStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationQueueRepository extends JpaRepository<NotificationQueue, Long> {

    List<NotificationQueue> findByTrangThaiOrderByCreatedAtAsc(NotificationQueueStatus trangThai);
}
