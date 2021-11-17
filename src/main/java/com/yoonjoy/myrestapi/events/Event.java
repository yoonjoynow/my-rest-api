package com.yoonjoy.myrestapi.events;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
; import javax.persistence.Id;

@Getter @Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Event {

    protected Event() {
    }

    @Id @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional) 이게 없으면 온라인 모임
    private int basePrice; // (optional)
    private int maxPrice; // (optional)
    private int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus = EventStatus.DRAFT;

    @Builder
    public Event(String name, String description, LocalDateTime beginEnrollmentDateTime, LocalDateTime closeEnrollmentDateTime, LocalDateTime beginEventDateTime, LocalDateTime endEventDateTime, String location, int basePrice, int maxPrice, int limitOfEnrollment, boolean offline, boolean free) {
        this.name = name;
        this.description = description;
        this.beginEnrollmentDateTime = beginEnrollmentDateTime;
        this.closeEnrollmentDateTime = closeEnrollmentDateTime;
        this.beginEventDateTime = beginEventDateTime;
        this.endEventDateTime = endEventDateTime;
        this.location = location;
        this.basePrice = basePrice;
        this.maxPrice = maxPrice;
        this.limitOfEnrollment = limitOfEnrollment;
        this.offline = offline;
        this.free = free;
        update();
    }

    public void update() {
        checkFree();
        checkOffline();
    }

    private void checkFree() {
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        }
    }

    private void checkOffline() {
        if (this.location != null || this.location.isBlank()) {
            this.offline = true;
        }
    }

}
