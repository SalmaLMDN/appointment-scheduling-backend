package com.salma.appointments_management.booking;


import com.salma.appointments_management.booking.dto.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingResponse toResponse(Booking booking) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getId());
        bookingResponse.setPatientId(booking.getPatient().getId());
        bookingResponse.setSlotId(booking.getSlot().getId());
        bookingResponse.setSlotStartAt(booking.getSlot().getStartAt());
        bookingResponse.setSlotEndAt(booking.getSlot().getEndAt());
        bookingResponse.setCreatedAt(booking.getCreatedAt());
        bookingResponse.setStatus(booking.getStatus());
        return bookingResponse;
    }
}
