package com.sweetdelight.api.service;

import com.sweetdelight.api.dto.BookingDecisionRequest;
import com.sweetdelight.api.dto.CreateBookingRequest;
import com.sweetdelight.api.dto.PartyRoomDto;
import com.sweetdelight.api.dto.RoomBookingDto;
import com.sweetdelight.api.entity.BookingStatus;
import com.sweetdelight.api.entity.PartyRoom;
import com.sweetdelight.api.entity.RoomBooking;
import com.sweetdelight.api.exception.BadRequestException;
import com.sweetdelight.api.exception.ConflictException;
import com.sweetdelight.api.exception.ResourceNotFoundException;
import com.sweetdelight.api.repository.PartyRoomRepository;
import com.sweetdelight.api.repository.RoomBookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PartyRoomService {

    private final PartyRoomRepository partyRoomRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final NotificationService notificationService;

    public PartyRoomService(PartyRoomRepository partyRoomRepository,
                            RoomBookingRepository roomBookingRepository,
                            NotificationService notificationService) {
        this.partyRoomRepository = partyRoomRepository;
        this.roomBookingRepository = roomBookingRepository;
        this.notificationService = notificationService;
    }

    public List<PartyRoomDto> getActiveRooms() {
        return partyRoomRepository.findByActiveTrue().stream()
                .map(r -> new PartyRoomDto(r.getId(), r.getName(), r.getDescription(), r.getCapacity(), r.isActive()))
                .collect(Collectors.toList());
    }

    public static boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    @Transactional
    public RoomBookingDto createBooking(UUID customerId, CreateBookingRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("Start time must be before end time");
        }

        PartyRoom room = partyRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Party room not found with id: " + request.getRoomId()));

        if (!room.isActive()) {
            throw new BadRequestException("Selected party room is currently unavailable for bookings");
        }

        if (request.getGuestCount() > room.getCapacity()) {
            throw new BadRequestException("Guest count (" + request.getGuestCount() + ") exceeds room capacity of " + room.getCapacity());
        }

        // Overlap verification
        List<RoomBooking> overlapping = roomBookingRepository.findOverlappingBookings(
                room.getId(),
                request.getBookingDate(),
                request.getStartTime(),
                request.getEndTime(),
                List.of(BookingStatus.PENDING, BookingStatus.APPROVED)
        );

        if (!overlapping.isEmpty()) {
            throw new ConflictException(String.format(
                    "Party room '%s' is already booked on %s between %s and %s. Please select another slot.",
                    room.getName(), request.getBookingDate(), overlapping.get(0).getStartTime(), overlapping.get(0).getEndTime()
            ));
        }

        RoomBooking booking = new RoomBooking();
        booking.setRoom(room);
        booking.setCustomerId(customerId);
        booking.setBookingDate(request.getBookingDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setGuestCount(request.getGuestCount());
        booking.setContactName(request.getContactName());
        booking.setContactPhone(request.getContactPhone());
        booking.setStatus(BookingStatus.PENDING);
        booking.setNotes(request.getNotes());

        RoomBooking saved = roomBookingRepository.save(booking);
        return mapToDto(saved);
    }

    public List<RoomBookingDto> getCustomerBookings(UUID customerId) {
        return roomBookingRepository.findByCustomerIdOrderByBookingDateDescStartTimeDesc(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<RoomBookingDto> getAllBookings() {
        return roomBookingRepository.findAllByOrderByBookingDateDescStartTimeDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomBookingDto updateDecision(Long id, BookingDecisionRequest request) {
        RoomBooking booking = roomBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));

        if (request.getStatus() != BookingStatus.APPROVED && request.getStatus() != BookingStatus.REJECTED) {
            throw new BadRequestException("Decision status must be either APPROVED or REJECTED");
        }

        // If approving, re-verify no conflicting APPROVED booking has been committed
        if (request.getStatus() == BookingStatus.APPROVED) {
            List<RoomBooking> conflicts = roomBookingRepository.findOverlappingBookings(
                    booking.getRoom().getId(),
                    booking.getBookingDate(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    List.of(BookingStatus.APPROVED)
            );
            // Filter out self
            boolean hasOtherApproved = conflicts.stream().anyMatch(b -> !b.getId().equals(booking.getId()));
            if (hasOtherApproved) {
                throw new ConflictException("Another booking has already been APPROVED for this time slot.");
            }
        }

        booking.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            booking.setNotes(request.getNotes());
        }

        RoomBooking saved = roomBookingRepository.save(booking);

        // Notify customer
        notificationService.notifyBookingDecision(saved);

        return mapToDto(saved);
    }

    public RoomBookingDto mapToDto(RoomBooking booking) {
        RoomBookingDto dto = new RoomBookingDto();
        dto.setId(booking.getId());
        dto.setRoomId(booking.getRoom() != null ? booking.getRoom().getId() : null);
        dto.setRoomName(booking.getRoom() != null ? booking.getRoom().getName() : null);
        dto.setCustomerId(booking.getCustomerId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setGuestCount(booking.getGuestCount());
        dto.setContactName(booking.getContactName());
        dto.setContactPhone(booking.getContactPhone());
        dto.setStatus(booking.getStatus());
        dto.setNotes(booking.getNotes());
        dto.setCreatedAt(booking.getCreatedAt());
        return dto;
    }
}
