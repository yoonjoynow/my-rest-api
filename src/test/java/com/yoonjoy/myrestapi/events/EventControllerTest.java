package com.yoonjoy.myrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
//@WebMvcTest // 웹 컨트롤러 계층용 테스트므로 리파지토리는 자동으로 빈으로 등록되지 않는다.
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    //@MockBean // eventRepository를 MockBean으로 만들어주자
    //EventRepository eventRepository;

    @Test
    @DisplayName("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception   {
        EventDto eventDto = EventDto.builder()
                .name("Yoon Soyi")
                .description("is beautiful!!!!")
                .beginEnrollmentDateTime(LocalDateTime.of(2000, 6, 13, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 27, 11, 10))
                .beginEventDateTime(LocalDateTime.of(1996, 4, 4, 4, 4))
                .endEventDateTime(LocalDateTime.of(2021, 3, 30, 15, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("트라펠리스 104동 3202호")
                .build();

        // eventRepository는 Mock객체이므로 모든 메소드에 대한 리턴값이 null이다.
        // 따라서 제대로된 값을 받아오기 위해선 다음과 같은 행위가 필요하다.
        // eventRepository의 save가 호출되면 event를 리턴하라
        // 이러한 행위 (~~한 경우엔 ~~하게 동작하라)를 스터빙(stubbing)이라고 한다.
        // event.setId(10);
        // Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    // event 객체를 objectMapper의 writeValueAsString를 통해 json문자열로 변환해 요청 본문에 담는다
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    @DisplayName("입력받을 수 없는 값을 받은 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception   {
        Event event = Event.builder()
                .id(100)
                .name("Yoon Soyi")
                .description("is beautiful!!!!")
                .beginEnrollmentDateTime(LocalDateTime.of(2000, 6, 13, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 9, 27, 11, 10))
                .beginEventDateTime(LocalDateTime.of(1996, 4, 4, 4, 4))
                .endEventDateTime(LocalDateTime.of(2021, 3, 30, 15, 30))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("트라펠리스 104동 3202호")
                .free(true) //위에서 가격을 설정해둿는데 가격을 무료로 지정한 잘못된 값. 입력값 제한이 필요
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                // event 객체를 objectMapper의 writeValueAsString를 통해 json문자열로 변환해 요청 본문에 담는다
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 비어있는 경우 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("입력값이 잘못된 경우 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Yoon Soyi")
                .description("is beautiful!!!!")
                .beginEnrollmentDateTime(LocalDateTime.of(2000, 6, 13, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(1870, 9, 27, 11, 10))
                .beginEventDateTime(LocalDateTime.of(1996, 4, 4, 4, 4))
                .endEventDateTime(LocalDateTime.of(2021, 3, 30, 15, 30))
                .basePrice(1000)
                .maxPrice(40)
                .limitOfEnrollment(100)
                .location("트라펠리스 104동 3202호")
                .build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

}