package com.yoonjoy.myrestapi.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    void builder() {
        Event event = Event.builder()
                .name("우아한 테크코스 4기")
                .description("우테코 4기 백엔드 전형 모집 시작")
                .build();

        assertThat(event).isNotNull();
    }

    @DisplayName("오프라인 여부 테스트")
    @ParameterizedTest(name = "{displayName} : location = {0}")
    @ValueSource(strings = {"강남역", "사당역", " ", ""})
    @NullAndEmptySource
    void testOffline(String location) {
        Event event = Event.builder()
                .location(location)
                .build();

        assertThat(event.isOffline()).isTrue();
    }

    @DisplayName("무료 이벤트 여부 테스트")
    @ParameterizedTest(name = "{displayName}")
    @CsvSource({
            "0, 0, true",
            "100, 0, false",
            "0, 100, false"
    })
    void testFree(int basePrice, int maxPrice, boolean isFree) {
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        assertThat(event.isFree()).isEqualTo(isFree);
    }

}