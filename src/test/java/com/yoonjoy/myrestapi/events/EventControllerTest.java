package com.yoonjoy.myrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext context;

    @Autowired
    EventRepository eventRepository;

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("없는 필드를 포함해 이벤트를 생성하는 테스트, 실패")
    void createEvent_withWrongFields() throws Exception {
        Event event = Event.builder()
                .name("우아한 테크코스 4기")
                .description("우테코 4기 백엔드 과정 모집")
                .beginEventDateTime(LocalDateTime.of(2021, 11, 1, 10, 30))
                .endEventDateTime(LocalDateTime.of(2021, 11, 12, 10, 30))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 12, 1, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 12, 12, 10, 30))
                .maxPrice(100)
                .basePrice(0)
                .location("선릉역")
                .free(false)
                .offline(false)
                .build();
        event.setId(20);
        event.setEventStatus(EventStatus.PUBLISHED);

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists());
    }

    @Test
    @DisplayName("올바르지 않은 값으로 이벤트를 생성하는 테스트, 실패")
    void createEvent_withWrongValues() throws Exception {
        EventDto.Create event = EventDto.Create.builder()
                .name("우아한 테크코스 4기")
                .description("우테코 4기 백엔드 과정 모집")
                .beginEventDateTime(LocalDateTime.of(2021, 11, 1, 10, 30))
                .endEventDateTime(LocalDateTime.of(2021, 4, 12, 10, 30))
                .beginEnrollmentDateTime(LocalDateTime.of(2002, 12, 1, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2002, 6, 12, 10, 30))
                .maxPrice(0)
                .basePrice(100)
                .location("선릉역")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
                .andExpect(jsonPath("$[0].rejectedValue").exists());
    }

    @Test
    @DisplayName("정상적 이벤트를 생성하는 테스트")
    void createEvent() throws Exception {
        EventDto.Create event = EventDto.Create.builder()
                .name("우아한 테크코스 4기")
                .description("우테코 4기 백엔드 과정 모집")
                .beginEventDateTime(LocalDateTime.of(2021, 11, 1, 10, 30))
                .endEventDateTime(LocalDateTime.of(2022, 11, 12, 10, 30))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 12, 1, 10, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 12, 12, 10, 30))
                .limitOfEnrollment(50)
                .maxPrice(0)
                .basePrice(0)
                .location("선릉역")
                .build();

        this.mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("free").value(true))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andExpect(jsonPath("_links.query-events").exists());
    }

}
