package com.yoonjoy.myrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@ActiveProfiles("test")
@SpringBootTest
class EventControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris().host("yoonjoy.me").removePort(), prettyPrint())
                        .withResponseDefaults(modifyUris().host("yoonjoy.me").removePort(), prettyPrint()))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("없는 필드를 포함해 이벤트를 생성하는 테스트, 실패")
    void hello() throws Exception {
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
                .andExpect(status().isBadRequest());
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
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("errors[0].rejectedValue").exists())
                .andExpect(jsonPath("_links.index").exists());
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
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("해당 이벤트 조회"),
                                linkWithRel("update-event").description("해당 이벤트 수정"),
                                linkWithRel("query-events").description("이벤트 목록 조회"),
                                linkWithRel("profile").description("이벤트 생성 프로필")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("컨텐트 타입")
                        ),
                        requestFields(
                            fieldWithPath("name").type(JsonFieldType.STRING).description("이벤트의 이름"),
                            fieldWithPath("description").type(JsonFieldType.STRING).description("이벤트의 설명"),
                            fieldWithPath("beginEnrollmentDateTime").type(JsonFieldType.STRING).description("이벤트의 등록 시작 시간"),
                            fieldWithPath("closeEnrollmentDateTime").type(JsonFieldType.STRING).description("이벤트 등록 종료 시간"),
                            fieldWithPath("beginEventDateTime").type(JsonFieldType.STRING).description("이벤트 진행 시작 시간"),
                            fieldWithPath("endEventDateTime").type(JsonFieldType.STRING).description("이벤트 진행 종료 시간"),
                            fieldWithPath("location").type(JsonFieldType.STRING).description("이벤트 진행 위치"),
                            fieldWithPath("basePrice").type(JsonFieldType.NUMBER).description("이벤트 등록 기본 가격"),
                            fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("이벤트 등록 최대 가격"),
                            fieldWithPath("limitOfEnrollment").type(JsonFieldType.NUMBER).description("이벤트 등록 제한 인원")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 이벤트의 정보를 조회할 수 있는 URL"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("생성된 이벤트의 Content-Type")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 이벤트의 식별자"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성된 이벤트의 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("생성된 이벤트의 설명"),
                                fieldWithPath("beginEnrollmentDateTime").type(JsonFieldType.STRING).description("생성된 이벤트의 등록 시작 시간"),
                                fieldWithPath("closeEnrollmentDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 등록 종료 시간"),
                                fieldWithPath("beginEventDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 진행 시작 시간"),
                                fieldWithPath("endEventDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 진행 종료 시간"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("생성된 이벤트 진행 위치"),
                                fieldWithPath("basePrice").type(JsonFieldType.NUMBER).description("생성된 이벤트 등록 기본 가격"),
                                fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("생성된 이벤트 등록 최대 가격"),
                                fieldWithPath("limitOfEnrollment").type(JsonFieldType.NUMBER).description("생성된 이벤트 등록 제한 인원"),
                                fieldWithPath("free").type(JsonFieldType.BOOLEAN).description("생성된 이벤트 등록의 무료 여부"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("생성된 이벤트의 오프라인 진행 여부"),
                                fieldWithPath("eventStatus").type(JsonFieldType.STRING).description("생성된 이벤트의 진행 상태"),

                                //optional fields
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("생성된 이벤트 정보를 조회할 수 있는 URL").optional(),
                                fieldWithPath("_links.update-event.href").type(JsonFieldType.STRING).description("생성된 이벤트 정보를 수정할 수 있는 URL").optional(),
                                fieldWithPath("_links.query-events.href").type(JsonFieldType.STRING).description("이벤트 목록 조회할 수 있는 URL").optional(),
                                fieldWithPath("_links.profile.href").type(JsonFieldType.STRING).description("이벤트 생성 문서를 조회할 수 있는 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 페이징한 후 두번째 페이지 조회하기")
    void queryEvents() throws Exception {
        IntStream.range(0, 50).forEach(this::generateEvents);

        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self.href").exists());
    }

    @RepeatedTest(value = 50, name = "{currentRepetition}번째 이벤트 조회")
    @DisplayName("이벤트 단건 조회 성공 테스트")
    void getEvent(RepetitionInfo info) throws Exception {
        int index = info.getCurrentRepetition();
        generateEvents(50);

        this.mockMvc.perform(get("/api/events/{id}", index))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }

    private void generateEvents(int index) {
        int number = new Random().nextInt(9);
        int year = 2020 + number;
        EventDto.Create dto = EventDto.Create.builder()
                .name(index + "번째 이벤트")
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(year, 1, 10, 12, 30))
                .closeEnrollmentDateTime(LocalDateTime.of(year, 2, 25, 18, 30))
                .beginEventDateTime(LocalDateTime.of(year, 4, 4, 10, 0))
                .endEventDateTime(LocalDateTime.of(year, 4, 20, 12, 30))
                .location("강남역")
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(10 * year)
                .build();
        this.eventRepository.save(dto.toEntity());
    }

    @RepeatedTest(10)
    @DisplayName("없는 이벤트 조회시 404 응답받기")
    void getEventWithEmptyValue(RepetitionInfo info) {
        generateEvents(50);
        Integer id = 50 + info.getCurrentRepetition();
    }

}
