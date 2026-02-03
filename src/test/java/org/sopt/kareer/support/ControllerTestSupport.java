package org.sopt.kareer.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.sopt.kareer.domain.jobposting.controller.JobPostingController;
import org.sopt.kareer.domain.jobposting.service.JobPostingCrawler;
import org.sopt.kareer.domain.jobposting.service.JobPostingService;
import org.sopt.kareer.domain.member.service.MemberService;
import org.sopt.kareer.global.jwt.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {JobPostingController.class},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)
        })
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public abstract class ControllerTestSupport {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected JobPostingService jobPostingService;

    @MockBean
    protected JobPostingCrawler jobPostingCrawler;

    @MockBean
    protected JwtTokenUtil jwtTokenUtil;

    @MockBean
    protected MemberService memberService;

    @MockBean
    protected JpaMetamodelMappingContext mappingContext;

}
