package com.salma.appointments_management.availability;

import com.salma.appointments_management.availability.AvailabilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AvailabilityRuleRepository extends JpaRepository<AvailabilityRule, UUID> {
}
