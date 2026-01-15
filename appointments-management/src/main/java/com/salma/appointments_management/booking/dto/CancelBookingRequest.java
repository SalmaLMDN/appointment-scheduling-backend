package com.salma.appointments_management.booking.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelBookingRequest {
    @NotBlank
    private String idempotencyKey;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
