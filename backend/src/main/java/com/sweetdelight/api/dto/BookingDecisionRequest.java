package com.sweetdelight.api.dto;

import com.sweetdelight.api.entity.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class BookingDecisionRequest {

    @Schema(description = "Decision: APPROVED or REJECTED", example = "APPROVED")
    @NotNull(message = "Status decision is required")
    private BookingStatus status;

    @Schema(description = "Optional notes or reason for rejection/approval", example = "Looking forward to hosting your celebration!")
    private String notes;

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
}
