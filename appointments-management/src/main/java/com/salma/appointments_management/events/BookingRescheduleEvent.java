package com.salma.appointments_management.events;

import java.time.Instant;
import java.util.UUID;

public record BookingRescheduleEvent(
        UUID bookingId,
        UUID patientId,
        UUID slotId,
        Instant occuredAt
) {
}
