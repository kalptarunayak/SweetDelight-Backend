package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.BookingStatus;
import com.sweetdelight.api.entity.RoomBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    List<RoomBooking> findByCustomerIdOrderByBookingDateDescStartTimeDesc(UUID customerId);

    List<RoomBooking> findAllByOrderByBookingDateDescStartTimeDesc();

    @Query("SELECT rb FROM RoomBooking rb WHERE rb.room.id = :roomId " +
           "AND rb.bookingDate = :bookingDate " +
           "AND rb.status IN :statuses " +
           "AND rb.startTime < :endTime " +
           "AND rb.endTime > :startTime")
    List<RoomBooking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("statuses") Collection<BookingStatus> statuses
    );
}
