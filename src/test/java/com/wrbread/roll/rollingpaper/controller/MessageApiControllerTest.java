package com.wrbread.roll.rollingpaper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.service.MessageService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static com.wrbread.roll.rollingpaper.model.enums.IsPublic.PUBLIC;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MessageService messageService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 저장 테스트")
    void testSaveMessage() throws Exception {
        //given
        User user = User.builder()
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        Paper paper = new Paper(user, paperId, "title", PUBLIC);

        MessageDto messageDto = new MessageDto();
        messageDto.setName("name");
        messageDto.setContent("Lorem Ipsum is dummy text used in the printing and typesetting industry since the 1500s.");

        Message message = new Message(user, paper, 1L, messageDto.getName(), messageDto.getContent(), messageDto.getLikeCount());
        given(messageService.saveMessages(any(Long.class), any(MessageDto.class))).willReturn(message);

        String content = objectMapper.writeValueAsString(messageDto);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}/messages")
                .buildAndExpand(paperId)
                .toUri();

        //when
        ResultActions actions = mockMvc.perform(post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );

        //then
        actions
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/papers/" + paperId+ "/messages")));

    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 조회 테스트")
    void testGetMessage() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        Paper paper = new Paper(user, paperId, "title", PUBLIC);

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setPaperId(paperId);
        messageDto.setName("name");
        messageDto.setContent("Lorem Ipsum is dummy text used in the printing and typesetting industry since the 1500s.");

        given(messageService.getMessage(any(Long.class), any(Long.class))).willReturn(messageDto.toEntity(user, paper));

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}/messages/{message-id}")
                .buildAndExpand(paperId,messageId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(messageDto.getId()))
                .andExpect(jsonPath("$.paperId").value(messageDto.getPaperId()))
                .andExpect(jsonPath("$.name").value(messageDto.getName()))
                .andExpect(jsonPath("$.content").value(messageDto.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 수정 테스트")
    void testUpdateMessage() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        Paper paper = new Paper(user, paperId, "title", PUBLIC);

        Long messageId = 1L;
        MessageDto updatedMessageDto = new MessageDto();
        updatedMessageDto.setId(messageId);
        updatedMessageDto.setPaperId(paperId);
        updatedMessageDto.setName("name");
        updatedMessageDto.setContent("Lorem Ipsum is dummy text");

        given(messageService.updateMessage(any(Long.class), any(Long.class), any(MessageDto.class)))
                .willReturn(new Message(user, paper, messageId, updatedMessageDto.getName(), updatedMessageDto.getContent(), updatedMessageDto.getLikeCount()));


        String content = objectMapper.writeValueAsString(updatedMessageDto);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}/messages/{message-id}")
                .buildAndExpand(paperId,messageId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(patch(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content )
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedMessageDto.getId()))
                .andExpect(jsonPath("$.paperId").value(updatedMessageDto.getPaperId()))
                .andExpect(jsonPath("$.name").value(updatedMessageDto.getName()))
                .andExpect(jsonPath("$.content").value(updatedMessageDto.getContent()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("특정 롤링페이퍼의 전체 메시지 조회 테스트")
    void testGetMessages() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Long paperId = 1L;
        Paper paper = new Paper(user, paperId, "title", PUBLIC);

        List<Message> messages = Arrays.asList(
                new Message(user, paper, 1L, "name1", "Test Content1", 0),
                new Message(user, paper, 2L, "name2", "Test Content2", 0)
        );

        given(messageService.getMessages(paperId)).willReturn(messages);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}/messages")
                .buildAndExpand(paperId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(get(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        );

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(messages.get(0).getId()))
                .andExpect(jsonPath("$[0].paperId").value(messages.get(0).getPaper().getId()))
                .andExpect(jsonPath("$[0].name").value(messages.get(0).getName().toString()))
                .andExpect(jsonPath("$[0].content").value(messages.get(0).getContent().toString()))
                .andExpect(jsonPath("$[1].id").value(messages.get(1).getId()))
                .andExpect(jsonPath("$[1].paperId").value(messages.get(1).getPaper().getId()))
                .andExpect(jsonPath("$[1].name").value(messages.get(1).getName().toString()))
                .andExpect(jsonPath("$[1].content").value(messages.get(1).getContent().toString()));

    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 삭제 테스트")
    void testDeleteMessage() throws Exception {
        // Given
        Long paperId = 1L;

        Long messageId = 1L;

        doNothing().when(messageService).deleteMessage(paperId, messageId);
        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/papers/{paper-id}/messages/{message-id}")
                .buildAndExpand(paperId, messageId)
                .toUri();

        // When
        ResultActions actions = mockMvc.perform(delete(uri));

        // Then
        actions
                .andExpect(status().isNoContent());
    }
}