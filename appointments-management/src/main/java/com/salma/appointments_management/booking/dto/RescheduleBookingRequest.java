package com.salma.appointments_management.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class RescheduleBookingRequest {
    @NotNull
    private UUID newSlotId;

    @NotBlank
    private String idempotencyKey;


    public UUID getNewSlotId() {
        return newSlotId;
    }

    public void setNewSlotId(UUID newSlotId) {
        this.newSlotId = newSlotId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
