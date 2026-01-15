package com.salma.appointments_management.booking;


import com.salma.appointments_management.patient.Patient;
import com.salma.appointments_management.slot.Slot;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table (
        name="booking",
        uniqueConstraints = {
                // A slot can have at most one booking
                @UniqueConstraint(
                        name = "uk_booking_slot",
                        columnNames = {"slot_id"}
                ),
                // idempotency per (patient, idempotency_key)
                @UniqueConstraint(
                        name = "uk_booking_patient_idempotency",
                        columnNames = {"patient_id","idempotency_key"}
                )
        }
)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @JoinColumn(name="patient_id",nullable=false)
    private Patient patient;
    @ManyToOne
    @JoinColumn(name="slot_id",nullable=false)
    private Slot slot;
    @Column(nullable=false)
    private String status;
    @Column(name = "idempotency_key", nullable=false, length = 100)
    private String idempotencyKey;
    private String lastOperationKey;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLastOperationKey() {
        return lastOperationKey;
    }

    public void setLastOperationKey(String lastOperationKey) {
        this.lastOperationKey = lastOperationKey;
    }
}
