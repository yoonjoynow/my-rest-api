package com.yoonjoy.myrestapi.events;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EventDto {

    @Getter
    public static class Create {

        protected Create() {
        }

        @NotBlank
        private String name;

        private String description;
        @NotNull
        private LocalDateTime beginEnrollmentDateTime;
        @NotNull
        private LocalDateTime closeEnrollmentDateTime;
        @NotNull
        private LocalDateTime beginEventDateTime;
        @NotNull
        private LocalDateTime endEventDateTime;

        private String location; // (optional) 이게 없으면 온라인 모임
        @Min(0)
        private int basePrice; // (optional)
        @Min(0)
        private int maxPrice; // (optional)
        @Min(0)
        private int limitOfEnrollment;

        @Builder
        public Create(String name, String description, LocalDateTime beginEnrollmentDateTime, LocalDateTime closeEnrollmentDateTime, LocalDateTime beginEventDateTime, LocalDateTime endEventDateTime, String location, int basePrice, int maxPrice, int limitOfEnrollment) {
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
        }

        public Event toEntity() {
            return Event.builder()
                    .name(this.name)
                    .description(this.description)
                    .beginEnrollmentDateTime(this.beginEnrollmentDateTime)
                    .closeEnrollmentDateTime(this.closeEnrollmentDateTime)
                    .beginEventDateTime(this.beginEventDateTime)
                    .endEventDateTime(this.endEventDateTime)
                    .location(this.location)
                    .basePrice(this.basePrice)
                    .maxPrice(this.maxPrice)
                    .limitOfEnrollment(this.limitOfEnrollment)
                    .build();
        }
    }

    public static class Response {
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
    }

}
