package com.salma.appointments_management.events;

import com.salma.appointments_management.events.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;



@Slf4j
@Component
@RequiredArgsConstructor
public class BookingEventListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent bookingConfirmedEvent) {
        notificationService.sendBookingConfirmed(bookingConfirmedEvent);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCancelled(BookingCancelledEvent bookingCancelledEvent) {
        notificationService.sendBookingCancelled(bookingCancelledEvent);
    }

    @TransactionalEventListener( phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingReschedule(BookingRescheduleEvent bookingRescheduleEvent) {
        notificationService.sendBookingRescheduled(bookingRescheduleEvent);
    }


}
