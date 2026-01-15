package com.salma.appointments_management.booking;


import com.salma.appointments_management.patient.PatientRepository;
import com.salma.appointments_management.slot.Slot;
import com.salma.appointments_management.slot.SlotRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final PatientRepository patientRepository;


    public BookingService(BookingRepository bookingRepository, SlotRepository slotRepository, PatientRepository patientRepository) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.patientRepository = patientRepository;
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
        return bookingRepository.save(booking);

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
        return bookingRepository.save(booking);

    }
}
