package com.salma.appointments_management.events;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NotificationService {
    public void sendBookingConfirmed(BookingConfirmedEvent event) {
        log.info("BookingConfirmedEvent received : bookingId = {}, patientId = {}, slotId = {}, occuredAt = {}",
                event.bookingId(), event.patientId(), event.slotId(), event.occuredAt());
    }

    public void sendBookingCancelled(BookingCancelledEvent event) {
        log.info("BookingCancelledEvent received : bookingId = {}, patientId = {}, slotId = {}, occuredAt = {}",
                event.bookingId(), event.patientId(), event.slotId(), event.occuredAt());
        //throw new RuntimeException("Simulated notification Failure");
    }

    public void sendBookingRescheduled(BookingRescheduleEvent event) {
        log.info("BookingRescheduleEvent received : bookingId = {}, patientId = {}, slotId = {}, occuredAt = {}",
                event.bookingId(), event.patientId(), event.slotId(), event.occuredAt());
    }
}
