package com.sweetdelight.api.service;

import com.sweetdelight.api.dto.CreateBookingRequest;
import com.sweetdelight.api.entity.BookingStatus;
import com.sweetdelight.api.entity.PartyRoom;
import com.sweetdelight.api.entity.RoomBooking;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.exception.ConflictException;
import com.sweetdelight.api.repository.PartyRoomRepository;
import com.sweetdelight.api.repository.RoomBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomOverlapLogicTest {

    @Mock
    private PartyRoomRepository partyRoomRepository;

    @Mock
    private RoomBookingRepository roomBookingRepository;

    @Mock
    private NotificationService notificationService;

    private PartyRoomService partyRoomService;

    @BeforeEach
    void setUp() {
        partyRoomService = new PartyRoomService(partyRoomRepository, roomBookingRepository, notificationService);
    }

    @Test
    @DisplayName("Overlapping time logic: partial overlap should return true")
    void testPartialOverlap() {
        LocalTime start1 = LocalTime.of(14, 0);
        LocalTime end1 = LocalTime.of(16, 0);

        LocalTime start2 = LocalTime.of(15, 0);
        LocalTime end2 = LocalTime.of(17, 0);

        assertTrue(PartyRoomService.isTimeOverlapping(start1, end1, start2, end2));
        assertTrue(PartyRoomService.isTimeOverlapping(start2, end2, start1, end1));
    }

    @Test
    @DisplayName("Overlapping time logic: enclosed slot should return true")
    void testEnclosedOverlap() {
        LocalTime outerStart = LocalTime.of(14, 0);
        LocalTime outerEnd = LocalTime.of(18, 0);

        LocalTime innerStart = LocalTime.of(15, 0);
        LocalTime innerEnd = LocalTime.of(16, 0);

        assertTrue(PartyRoomService.isTimeOverlapping(outerStart, outerEnd, innerStart, innerEnd));
        assertTrue(PartyRoomService.isTimeOverlapping(innerStart, innerEnd, outerStart, outerEnd));
    }

    @Test
    @DisplayName("Adjacent touching booking slots (e.g. 14:00-16:00 and 16:00-18:00) should NOT overlap")
    void testAdjacentBookingsDoNotOverlap() {
        LocalTime slot1Start = LocalTime.of(14, 0);
        LocalTime slot1End = LocalTime.of(16, 0);

        LocalTime slot2Start = LocalTime.of(16, 0);
        LocalTime slot2End = LocalTime.of(18, 0);

        assertFalse(PartyRoomService.isTimeOverlapping(slot1Start, slot1End, slot2Start, slot2End));
        assertFalse(PartyRoomService.isTimeOverlapping(slot2Start, slot2End, slot1Start, slot1End));
    }

    @Test
    @DisplayName("Completely separated booking slots should NOT overlap")
    void testSeparatedBookingsDoNotOverlap() {
        LocalTime morningStart = LocalTime.of(10, 0);
        LocalTime morningEnd = LocalTime.of(12, 0);

        LocalTime eveningStart = LocalTime.of(17, 0);
        LocalTime eveningEnd = LocalTime.of(19, 0);

        assertFalse(PartyRoomService.isTimeOverlapping(morningStart, morningEnd, eveningStart, eveningEnd));
    }

    @Test
    @DisplayName("Booking service should throw ConflictException when overlapping booking exists in DB")
    void testCreateBookingConflictThrowsException() {
        PartyRoom room = new PartyRoom();
        room.setId(1L);
        room.setName("Celebration Suite");
        room.setCapacity(20);
        room.setActive(true);

        when(partyRoomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomBooking existingBooking = new RoomBooking();
        existingBooking.setId(10L);
        existingBooking.setRoom(room);
        existingBooking.setBookingDate(LocalDate.now().plusDays(2));
        existingBooking.setStartTime(LocalTime.of(14, 0));
        existingBooking.setEndTime(LocalTime.of(16, 0));
        existingBooking.setStatus(BookingStatus.APPROVED);

        when(roomBookingRepository.findOverlappingBookings(
                eq(1L),
                eq(LocalDate.now().plusDays(2)),
                eq(LocalTime.of(15, 0)),
                eq(LocalTime.of(17, 0)),
                any()
        )).thenReturn(List.of(existingBooking));

        CreateBookingRequest request = new CreateBookingRequest();
        request.setRoomId(1L);
        request.setBookingDate(LocalDate.now().plusDays(2));
        request.setStartTime(LocalTime.of(15, 0));
        request.setEndTime(LocalTime.of(17, 0));
        request.setGuestCount(8);
        request.setContactName("Meera");
        request.setContactPhone("+919876543210");

        assertThrows(ConflictException.class, () ->
                partyRoomService.createBooking(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("Booking service should reject when start time is after or equal to end time")
    void testInvalidStartEndTime() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setRoomId(1L);
        request.setBookingDate(LocalDate.now().plusDays(2));
        request.setStartTime(LocalTime.of(17, 0));
        request.setEndTime(LocalTime.of(16, 0));
        request.setGuestCount(5);

        assertThrows(BadRequestException.class, () ->
                partyRoomService.createBooking(UUID.randomUUID(), request));
    }
}
