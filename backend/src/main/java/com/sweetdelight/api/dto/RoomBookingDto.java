package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RoomBookingDto {

    @Schema(description = "Booking ID", example = "1")
    private Long id;

    @Schema(description = "Party Room ID", example = "1")
    private Long roomId;

    @Schema(description = "Room Name", example = "The Sweet Celebration Lounge")
    private String roomName;

    @Schema(description = "Customer UUID")
    private UUID customerId;

    @Schema(description = "Booking Date", example = "2026-11-15")
    private LocalDate bookingDate;

    @Schema(description = "Start Time", example = "16:00:00")
    private LocalTime startTime;

    @Schema(description = "End Time", example = "18:00:00")
    private LocalTime endTime;

    @Schema(description = "Guest Count", example = "10")
    private Integer guestCount;

    @Schema(description = "Contact Name", example = "Priya Kapoor")
    private String contactName;

    @Schema(description = "Contact Phone", example = "+919876543212")
    private String contactPhone;

    @Schema(description = "Booking Status", example = "APPROVED")
    private BookingStatus status;

    @Schema(description = "Booking Notes")
    private String notes;

    @Schema(description = "Created Timestamp")
    private OffsetDateTime createdAt;

    public RoomBookingDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getGuestCount() {
        return guestCount;
    }

    public void setGuestCount(Integer guestCount) {
        this.guestCount = guestCount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
