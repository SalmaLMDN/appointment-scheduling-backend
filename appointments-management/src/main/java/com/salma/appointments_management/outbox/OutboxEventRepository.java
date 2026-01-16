package com.salma.appointments_management.outbox;


import com.salma.appointments_management.enums.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent>  findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);
}
