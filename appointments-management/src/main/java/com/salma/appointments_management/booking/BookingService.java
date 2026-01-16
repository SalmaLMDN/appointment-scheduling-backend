package com.salma.appointments_management.booking;


import com.salma.appointments_management.enums.OutboxStatus;
import com.salma.appointments_management.events.BookingCancelledEvent;
import com.salma.appointments_management.events.BookingConfirmedEvent;
import com.salma.appointments_management.events.BookingRescheduleEvent;
import com.salma.appointments_management.outbox.OutboxEvent;
import com.salma.appointments_management.outbox.OutboxEventRepository;
import com.salma.appointments_management.patient.PatientRepository;
import com.salma.appointments_management.slot.Slot;
import com.salma.appointments_management.slot.SlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service

public class BookingService {
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;



    public BookingService(BookingRepository bookingRepository, SlotRepository slotRepository, PatientRepository patientRepository, ApplicationEventPublisher applicationEventPublisher,
                            OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.patientRepository = patientRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Booking bookSlot(UUID patientId, UUID slotId, String idempotencyKey) {
        Optional<Booking> existing = bookingRepository.findByPatientIdAndIdempotencyKey(patientId, idempotencyKey);
        if(existing.isPresent()) {
            return existing.get();
        }
        Slot slot = slotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        if(!slot.getStatus().equals("AVAILABLE")) {
            throw new IllegalStateException("Slot is not available");
        }

        slot.setStatus("BOOKED");
        slot.setUpdatedAt(Instant.now());
        slotRepository.save(slot);
        Booking booking = new Booking();
        booking.setIdempotencyKey(idempotencyKey);
        booking.setLastOperationKey(idempotencyKey);
        booking.setSlot(slot);
        booking.setStatus("CONFIRMED");
        booking.setPatient(patientRepository.getById(patientId));
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());
        Booking saved = bookingRepository.save(booking);
        outboxEventRepository.save(newOutboxEvent("BOOKING_CONFIRMED",saved));
        applicationEventPublisher.publishEvent(new BookingConfirmedEvent(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getSlot().getId(),
                Instant.now()
        ));
        return saved;

    }

    @Transactional
    public Booking rescheduleSlot(UUID bookingId, UUID slotId, String idempotencyKey) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if(idempotencyKey.equals(booking.getLastOperationKey())) return booking;
        Slot newSlot = slotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        Slot oldSlot = booking.getSlot();
        if(!newSlot.getStatus().equals("AVAILABLE")) {
            throw new IllegalStateException("Slot is not available");
        }
        oldSlot.setStatus("AVAILABLE");
        oldSlot.setUpdatedAt(Instant.now());
        slotRepository.save(oldSlot);
        newSlot.setStatus("BOOKED");
        newSlot.setUpdatedAt(Instant.now());
        slotRepository.save(newSlot);
        booking.setSlot(newSlot);
        booking.setStatus("CONFIRMED");
        booking.setUpdatedAt(Instant.now());
        booking.setLastOperationKey(idempotencyKey);
        Booking saved =  bookingRepository.save(booking);
        applicationEventPublisher.publishEvent(new BookingRescheduleEvent(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getSlot().getId(),
                Instant.now()
        ));
        return saved;

    }

    @Transactional
    public Booking cancelSlot(UUID bookingId, String idempotencyKey) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if(booking.getStatus().equals("CANCELLED") && idempotencyKey.equals(booking.getLastOperationKey())) return booking;
        if(!booking.getStatus().equals("CONFIRMED")) { throw new IllegalStateException("Booking is not confirmed"); }

        Slot slot = slotRepository.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));
        slot.setStatus("AVAILABLE");
        slot.setUpdatedAt(Instant.now());
        slotRepository.save(slot);
        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(Instant.now());
        booking.setLastOperationKey(idempotencyKey);
        Booking saved = bookingRepository.save(booking);
        outboxEventRepository.save(newOutboxEvent("BOOKING_CANCELLED",saved));
        applicationEventPublisher.publishEvent(new BookingCancelledEvent(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getSlot().getId(),
                Instant.now()
        ));
        return saved;
    }

    // Helper method
    private OutboxEvent newOutboxEvent(String eventType, Booking booking) {
        OutboxEvent outboxEvent = new OutboxEvent();

        UUID eventId = UUID.randomUUID();
        outboxEvent.setId(eventId);

        outboxEvent.setEventType(eventType);
        outboxEvent.setAggregateId(booking.getId());
        outboxEvent.setAggregateType("BOOKING");
        outboxEvent.setCreatedAt(Instant.now());

        outboxEvent.setStatus(OutboxStatus.NEW);
        outboxEvent.setAttempts(0);

        // minimal payload as JSON
        try{
            String payload = objectMapper.writeValueAsString(
                    Map.of(
                            "eventId", eventId.toString(),
                            "bookingId", booking.getId().toString(),
                            "patientId", booking.getPatient().getId().toString(),
                            "slotId", booking.getSlot().getId().toString(),
                            "occuredAt", Instant.now().toString()
                    )
            );
            outboxEvent.setPayload(payload);
        } catch(Exception e) {
            throw new IllegalStateException("Failed to serialize outbox payload", e);
        }
        return outboxEvent;
    }
}
