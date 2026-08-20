package org.sopt.kareer.domain.roadmap.controller;

import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.roadmap.facade.ActionItemFacade;
import org.sopt.kareer.support.ControllerTestSupport;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActionItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ActionItemControllerTest extends ControllerTestSupport {

    private static final String BASE_URL = "/api/v1/roadmap/action-items";

    @MockBean
    private ActionItemFacade actionItemFacade;

    @Test
    void 사용자_Todo를_생성한다() throws Exception {
        String request = objectMapper.writeValueAsString(Map.of(
                "type", "CAREER",
                "title", "이력서 작성하기",
                "deadline", LocalDate.now().plusDays(1).toString()
        ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("액션 아이템이 생성되었습니다."));
    }

    @Test
    void 생성할_때_타입은_필수다() throws Exception {
        String request = objectMapper.writeValueAsString(Map.of(
                "title", "이력서 작성하기",
                "deadline", LocalDate.now().plusDays(1).toString()
        ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 생성할_때_마감일은_오늘보다_미래여야_한다() throws Exception {
        String request = objectMapper.writeValueAsString(Map.of(
                "type", "VISA",
                "title", "비자 서류 준비하기",
                "deadline", LocalDate.now().toString()
        ));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 제목만_전달해_Todo를_수정한다() throws Exception {
        String request = objectMapper.writeValueAsString(Map.of(
                "title", "수정된 제목"
        ));

        mockMvc.perform(patch(BASE_URL + "/{actionItemId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("액션 아이템이 수정되었습니다."));
    }

    @Test
    void 수정할_때_제목과_마감일이_모두_없으면_실패한다() throws Exception {
        mockMvc.perform(patch(BASE_URL + "/{actionItemId}", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void ActionItem을_삭제한다() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{actionItemId}", 10L))
                .andExpect(status().isNoContent());
    }
}
