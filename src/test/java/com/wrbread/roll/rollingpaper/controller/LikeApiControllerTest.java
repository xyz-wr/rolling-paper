package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class LikeApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("like 테스트")
    void testAcceptInvitation() throws Exception {
        // given
        Long messageId = 1L;

        doNothing().when(likeService).like(messageId);
        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/messages/{message-id}/likes")
                .buildAndExpand(messageId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.post(uri));

        // then
        actions
                .andExpect(status().isOk());
    }

}