package com.salma.appointments_management.booking;


import com.salma.appointments_management.booking.dto.BookingRequest;
import com.salma.appointments_management.booking.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(BookingService bookingService, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        Booking booking = bookingService.bookSlot(
                bookingRequest.getPatientId(), bookingRequest.getSlotId(), bookingRequest.getIdempotencyKey()
        );
        BookingResponse bookingResponse = bookingMapper.toResponse(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }
}
