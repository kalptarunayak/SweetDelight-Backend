package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateBookingRequest {

    @Schema(description = "Party Room ID", example = "1")
    @NotNull(message = "Room ID is required")
    private Long roomId;

    @Schema(description = "Booking Date (YYYY-MM-DD)", example = "2026-11-15")
    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be today or in the future")
    private LocalDate bookingDate;

    @Schema(description = "Start Time (HH:mm)", example = "16:00:00")
    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @Schema(description = "End Time (HH:mm)", example = "18:00:00")
    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Schema(description = "Number of guests (for cake cutting celebration)", example = "10")
    @NotNull(message = "Guest count is required")
    @Min(value = 1, message = "Guest count must be at least 1")
    private Integer guestCount;

    @Schema(description = "Contact Person Name", example = "Priya Kapoor")
    @NotBlank(message = "Contact name is required")
    private String contactName;

    @Schema(description = "Contact Phone Number (E.164)", example = "+919876543212")
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;

    @Schema(description = "Special celebration notes", example = "Cake cutting for 5th wedding anniversary.")
    private String notes;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
