package com.salma.appointments_management.config;

import com.salma.appointments_management.doctor.Doctor;
import com.salma.appointments_management.doctor.DoctorRepository;
import com.salma.appointments_management.patient.Patient;
import com.salma.appointments_management.patient.PatientRepository;
import com.salma.appointments_management.slot.Slot;
import com.salma.appointments_management.slot.SlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
public class SeedConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeedConfig.class);

    @Bean
    CommandLineRunner seedData(DoctorRepository doctorRepository, PatientRepository patientRepository, SlotRepository slotRepository) {
        return args -> {

            // Doctor
            Doctor doctor = new Doctor();
            doctor.setName("Dr Test");
            doctor.setTimezone("Europe/Paris");
            doctor.setCreatedAt(Instant.now());
            doctor.setUpdatedAt(Instant.now());
            doctor = doctorRepository.save(doctor);

            // Patient
            Patient patient = new Patient();
            patient.setName("Test Patient");
            patient.setTimezone("Europe/Paris");
            patient.setCreatedAt(Instant.now());
            patient.setUpdatedAt(Instant.now());
            patient = patientRepository.save(patient);

            // Slot: now + 1 hour, 30 min duration
            Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
            Instant end = start.plus(30, ChronoUnit.MINUTES);

            Slot slot = new Slot();
            slot.setCreatedAt(Instant.now());
            slot.setDoctor(doctor);
            slot.setStatus("AVAILABLE");
            slot.setStartAt(start);
            slot.setEndAt(end);
            slot.setUpdatedAt(Instant.now());
            slot = slotRepository.save(slot);

            LOGGER.info("Seeded slot with patient {} and slot  {}",patient.getId(), slot.getId());
        };
    }
}
