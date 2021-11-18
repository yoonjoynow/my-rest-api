package com.yoonjoy.myrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
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
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
class EventControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocs) {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(documentationConfiguration(restDocs))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())))
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
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
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
                .andExpect(jsonPath("errors[0].rejectedValue").exists());
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
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-event").description("link to update event"),
                                linkWithRel("profile").description("프로필")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("이벤트의 이름"),
                                fieldWithPath("description").description("이벤트의 설명"),
                                fieldWithPath("beginEnrollmentDateTime").description("이벤트 접수 시작 기간"),
                                fieldWithPath("closeEnrollmentDateTime").description("이벤트 접수 종료 기간"),
                                fieldWithPath("beginEventDateTime").description("이벤트 시작 기간"),
                                fieldWithPath("endEventDateTime").description("이벤트 종료 기간"),
                                fieldWithPath("location").description("이벤트 참여 위치"),
                                fieldWithPath("basePrice").description("이벤트 기본 격"),
                                fieldWithPath("maxPrice").description("이벤트 최대 가격"),
                                fieldWithPath("limitOfEnrollment").description("이벤트 참여 제한 인원")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 이벤트의 아이디"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("생성된이벤트의 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("생성된 이벤트의 설명"),
                                fieldWithPath("beginEnrollmentDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 접수 시작 기간"),
                                fieldWithPath("closeEnrollmentDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 접수 종료 기간"),
                                fieldWithPath("beginEventDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 시작 기간"),
                                fieldWithPath("endEventDateTime").type(JsonFieldType.STRING).description("생성된 이벤트 종료 기간"),
                                fieldWithPath("location").type(JsonFieldType.STRING).description("생성된 이벤트 참여 위치"),
                                fieldWithPath("basePrice").type(JsonFieldType.NUMBER).description("생성된 이벤트 기본 가격"),
                                fieldWithPath("maxPrice").type(JsonFieldType.NUMBER).description("생성된 이벤트 최대 가격"),
                                fieldWithPath("limitOfEnrollment").type(JsonFieldType.NUMBER).description("생성된 이벤트 참여 제한 인원"),
                                fieldWithPath("offline").type(JsonFieldType.BOOLEAN).description("생성된 이벤트 오프라인 여부"),
                                fieldWithPath("free").type(JsonFieldType.BOOLEAN).description("생성된 무료 이벤트 여부"),
                                fieldWithPath("eventStatus").type(JsonFieldType.STRING).description("생성된 이벤트 상태"),
                                fieldWithPath("_links.profile.href").type(JsonFieldType.STRING).description("사용자 프로필 조회"),

                                //optional fields
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("생성된 이벤트 조회").optional(),
                                fieldWithPath("_links.query-events.href").type(JsonFieldType.STRING).description("전체 이벤트 목록 조회").optional(),
                                fieldWithPath("_links.update-event.href").type(JsonFieldType.STRING).description("생성된 이벤트 수정").optional()
                        )
                ));
    }

}
