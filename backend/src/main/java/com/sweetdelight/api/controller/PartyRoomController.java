package com.sweetdelight.api.controller;

import com.sweetdelight.api.dto.BookingDecisionRequest;
import com.sweetdelight.api.dto.CreateBookingRequest;
import com.sweetdelight.api.dto.PartyRoomDto;
import com.sweetdelight.api.dto.RoomBookingDto;
import com.sweetdelight.api.security.UserPrincipal;
import com.sweetdelight.api.service.PartyRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Party Rooms", description = "Party room booking for cake cutting celebrations and approval workflow")
public class PartyRoomController {

    private final PartyRoomService partyRoomService;

    public PartyRoomController(PartyRoomService partyRoomService) {
        this.partyRoomService = partyRoomService;
    }

    @Operation(summary = "List party celebration rooms", description = "Public endpoint listing active celebration rooms designed for cake cutting and intimate gatherings.")
    @ApiResponse(responseCode = "200", description = "Party rooms retrieved")
    @GetMapping("/party-rooms")
    public ResponseEntity<List<PartyRoomDto>> getPartyRooms() {
        return ResponseEntity.ok(partyRoomService.getActiveRooms());
    }

    @Operation(summary = "Request party room reservation",
               description = "Creates a room booking for cake cutting. Overlapping bookings for the same room and time window are automatically rejected with 409 Conflict.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created with status PENDING"),
            @ApiResponse(responseCode = "400", description = "Invalid times or guest count exceeds capacity"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Bearer token required"),
            @ApiResponse(responseCode = "404", description = "Party room not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Room is already reserved during this time slot")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/party-rooms/bookings")
    public ResponseEntity<RoomBookingDto> createBooking(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateBookingRequest request) {
        RoomBookingDto created = partyRoomService.createBooking(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "View my party room bookings", description = "Retrieves all celebration room booking requests created by the authenticated customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/party-rooms/bookings/my")
    public ResponseEntity<List<RoomBookingDto>> getMyBookings(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(partyRoomService.getCustomerBookings(principal.getId()));
    }

    @Operation(summary = "List all room bookings (Admin)", description = "Retrieves all party room booking requests across the store. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bookings retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/party-rooms/bookings")
    public ResponseEntity<List<RoomBookingDto>> getAllBookings() {
        return ResponseEntity.ok(partyRoomService.getAllBookings());
    }

    @Operation(summary = "Approve or Reject room booking (Admin)",
               description = "Decides on a party room booking (APPROVED or REJECTED) and triggers an automated notification to the customer. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Booking status updated and customer notified"),
            @ApiResponse(responseCode = "400", description = "Invalid decision status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Another booking already approved for this time window")
    })
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/party-rooms/bookings/{id}/decision")
    public ResponseEntity<RoomBookingDto> decideBooking(
            @Parameter(description = "Booking ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody BookingDecisionRequest request) {
        return ResponseEntity.ok(partyRoomService.updateDecision(id, request));
    }
}
