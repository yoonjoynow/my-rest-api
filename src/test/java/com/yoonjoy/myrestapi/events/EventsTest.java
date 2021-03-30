package com.yoonjoy.myrestapi.events;

import lombok.Builder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventsTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("soyi")
                .description("is happy").build();
        assertThat(event).isNotNull();
    }
}