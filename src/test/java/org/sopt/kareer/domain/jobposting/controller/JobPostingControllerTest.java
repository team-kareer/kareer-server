package org.sopt.kareer.domain.jobposting.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingListResponse;
import org.sopt.kareer.domain.jobposting.dto.response.JobPostingResponse;
import org.sopt.kareer.domain.jobposting.entity.JobPosting;
import org.sopt.kareer.domain.jobposting.fixture.JobPostingFixture;
import org.sopt.kareer.support.ControllerTestSupport;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobPostingControllerTest extends ControllerTestSupport {

    @DisplayName("사용자의 정보를 바탕으로 채용공고를 추천해준다.")
    @Test
    void recommendJobPostings() throws Exception {

       //given
        JobPosting jobPosting1 = JobPostingFixture.getJobPosting(LocalDate.now());
        JobPosting jobPosting2 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(2));
        JobPosting jobPosting3 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(3));
        JobPosting jobPosting4 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(1));

        JobPostingResponse response1 = JobPostingResponse.from(jobPosting1, true);
        JobPostingResponse response2 = JobPostingResponse.from(jobPosting2, true);
        JobPostingResponse response3 = JobPostingResponse.from(jobPosting3, false);
        JobPostingResponse response4 = JobPostingResponse.from(jobPosting4, false);

        given(jobPostingFacade.recommend(
                any(),
                any(),
                anyBoolean()
        )).willReturn(new JobPostingListResponse(List.of(response1, response2, response3, response4)));

       //when && then
        mockMvc.perform(
                multipart("/api/v1/job-postings/recommend")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("채용 공고 추천에 성공하였습니다."))
                .andExpect(jsonPath("$.data.jobPostingResponses").isArray())
                .andExpect(jsonPath("$.data.jobPostingResponses.length()").value(4));
    }

    @DisplayName("채용공고 북마크를 추가/삭제한다.")
    @Test
    void createJobPostingBookmark() throws Exception {

       //given
        willDoNothing()
                .given(jobPostingFacade)
                .createOrDeleteBookmark(any(), any());
       //when && then
        mockMvc.perform(
                post("/api/v1/job-postings/1/bookmarks")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("채용 공고 북마크 추가 / 삭제에 성공했습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @DisplayName("채용공고 북마크를 조회한다.")
    @Test
    void getJobPostingBookmarks() throws Exception {

       //given
        JobPosting jobPosting1 = JobPostingFixture.getJobPosting(LocalDate.now());
        JobPosting jobPosting2 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(2));
        JobPosting jobPosting3 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(3));
        JobPosting jobPosting4 = JobPostingFixture.getJobPosting(LocalDate.now().plusDays(1));

        JobPostingResponse response1 = JobPostingResponse.from(jobPosting1, true);
        JobPostingResponse response2 = JobPostingResponse.from(jobPosting2, true);
        JobPostingResponse response3 = JobPostingResponse.from(jobPosting3, false);
        JobPostingResponse response4 = JobPostingResponse.from(jobPosting4, false);

        given(jobPostingFacade.getJobPostingBookmarks(any()))
                .willReturn(new JobPostingListResponse(List.of(response1, response2, response3, response4)));

       //when && then
        mockMvc.perform(
                get("/api/v1/job-postings/bookmarks")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크 채용 공고 조회에 성공하였습니다."))
                .andExpect(jsonPath("$.data.jobPostingResponses").isArray());
    }

}
