package com.wrbread.roll.rollingpaper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.service.PaperService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PaperApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaperService paperService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 저장")
    void testSavePaper() throws Exception {
        //given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        PaperDto paperDto = new PaperDto();
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = new Paper(user, 1L, paperDto.getTitle(), paperDto.getIsPublic());
        given(paperService.savePaper(any(PaperDto.class))).willReturn(paper);

        String content = objectMapper.writeValueAsString(paperDto);

        URI uri = UriComponentsBuilder.newInstance().path("/api/papers").build().toUri();

        // when
        ResultActions actions = mockMvc.perform(post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        // then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/papers")));
    }


    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 조회")
    void testGetPaper() throws Exception {
        // Given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        given(paperService.getPaper(paperId)).willReturn(paperDto.toEntity(user));

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}")
                .buildAndExpand(paperId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paperDto.getId()))
                .andExpect(jsonPath("$.title").value(paperDto.getTitle()))
                .andExpect(jsonPath("$.isPublic").value(paperDto.getIsPublic().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 수정")
    void testUpdatePaper() throws Exception {
        // Given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        PaperDto updatedPaperDto = new PaperDto();
        updatedPaperDto.setTitle("Updated Paper");
        updatedPaperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = Paper.builder()
                .user(user)
                .id(paperId)
                .title(updatedPaperDto.getTitle())
                .isPublic(updatedPaperDto.getIsPublic())
                .build();

        given(paperService.updatePaper(any(Long.class), any(PaperDto.class)))
                .willReturn(paper);


        String content= objectMapper.writeValueAsString(updatedPaperDto);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}")
                .buildAndExpand(paperId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(patch(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paperId))
                .andExpect(jsonPath("$.title").value(updatedPaperDto.getTitle()))
                .andExpect(jsonPath("$.isPublic").value(updatedPaperDto.getIsPublic().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("IsPublic이 PUBLIC인 롤링 페이퍼 전체 조회")
    void testGetPublicPapers() throws Exception {
        // Given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        List<Paper> publicPapers = Arrays.asList(
                new Paper(user, 1L, "Public Paper 1", IsPublic.PUBLIC),
                new Paper(user, 2L, "Public Paper 2", IsPublic.PUBLIC)
        );

        given(paperService.getPublicPapers()).willReturn(publicPapers);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/public-paper-list")
                .build()
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(publicPapers.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(publicPapers.get(0).getTitle()))
                .andExpect(jsonPath("$[0].isPublic").value(publicPapers.get(0).getIsPublic().toString()))
                .andExpect(jsonPath("$[1].id").value(publicPapers.get(1).getId()))
                .andExpect(jsonPath("$[1].title").value(publicPapers.get(1).getTitle()))
                .andExpect(jsonPath("$[1].isPublic").value(publicPapers.get(1).getIsPublic().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("IsPublic이 FRIEND인 롤링 페이퍼 전체 조회")
    void testGetFriendPapers() throws Exception {
        // Given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        List<Paper> friendPapers = Arrays.asList(
                new Paper(user, 1L, "Friend Paper 1", IsPublic.FRIEND),
                new Paper(user, 2L, "Friend Paper 2", IsPublic.FRIEND)
        );

        given(paperService.getFriendPapers()).willReturn(friendPapers);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/friend-paper-list")
                .build()
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(friendPapers.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(friendPapers.get(0).getTitle()))
                .andExpect(jsonPath("$[0].isPublic").value(friendPapers.get(0).getIsPublic().toString()))
                .andExpect(jsonPath("$[1].id").value(friendPapers.get(1).getId()))
                .andExpect(jsonPath("$[1].title").value(friendPapers.get(1).getTitle()))
                .andExpect(jsonPath("$[1].isPublic").value(friendPapers.get(1).getIsPublic().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 삭제")
    void testDeletePaper() throws Exception {
        // Given
        Long paperId = 1L;

        doNothing().when(paperService).deletePaper(paperId);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}")
                .buildAndExpand(paperId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(delete(uri));

        // Then
        actions
                .andExpect(status().isNoContent());
    }
}