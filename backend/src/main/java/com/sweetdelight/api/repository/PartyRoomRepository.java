package com.sweetdelight.api.repository;

import com.sweetdelight.api.entity.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {
    List<PartyRoom> findByActiveTrue();
}
