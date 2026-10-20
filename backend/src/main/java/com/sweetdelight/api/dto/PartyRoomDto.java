package com.sweetdelight.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class PartyRoomDto {

    @Schema(description = "Party Room ID", example = "1")
    private Long id;

    @Schema(description = "Room Name", example = "The Sweet Celebration Lounge")
    private String name;

    @Schema(description = "Description", example = "Private decorated lounge for cake cutting and intimate celebrations.")
    private String description;

    @Schema(description = "Seating / guest capacity", example = "16")
    private Integer capacity;

    @Schema(description = "Whether the room is currently active", example = "true")
    private boolean active;

    public PartyRoomDto() {}

    public PartyRoomDto(Long id, String name, String description, Integer capacity, boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
