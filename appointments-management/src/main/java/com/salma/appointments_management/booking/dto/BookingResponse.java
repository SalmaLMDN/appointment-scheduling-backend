package com.salma.appointments_management.booking.dto;

import java.time.Instant;
import java.util.UUID;

public class BookingResponse {
    private UUID id;
    private UUID patientId;
    private UUID slotId;
    private String status;
    private Instant slotStartAt;
    private Instant slotEndAt;
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public void setPatientId(UUID patientId) {
        this.patientId = patientId;
    }

    public UUID getSlotId() {
        return slotId;
    }

    public void setSlotId(UUID slotId) {
        this.slotId = slotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getSlotStartAt() {
        return slotStartAt;
    }

    public void setSlotStartAt(Instant slotStartAt) {
        this.slotStartAt = slotStartAt;
    }

    public Instant getSlotEndAt() {
        return slotEndAt;
    }

    public void setSlotEndAt(Instant slotEndAt) {
        this.slotEndAt = slotEndAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
