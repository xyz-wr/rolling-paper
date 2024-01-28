package com.wrbread.roll.rollingpaper.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.service.InvitationService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class InvitationApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvitationService invitationService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("초대장 발송 테스트")
    void testSendInvitation() throws Exception {
        //given
        Long paperId = 1L;
        String senEmail = "sender@gmail.com";
        String receiverEmail = "receiver@gmail.com";

        InvitationDto.Request request = new InvitationDto.Request();
        request.setRecEmail(receiverEmail);

        User sender = User.builder()
                .email(senEmail)
                .build();

        User receiver = User.builder()
                .email(receiverEmail)
                .build();

        Paper paper = Paper.builder()
                .id(paperId)
                .user(sender)
                .isPublic(IsPublic.FRIEND)
                .build();

        given(invitationService.invite(any(Long.class), any(InvitationDto.Request.class))).willReturn(request.toEntity(sender, receiver, paper, InvitationStatus.PENDING));

        String jsonPaperDto = objectMapper.writeValueAsString(request);
        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/invitations/papers/{paper-id}/invite")
                .buildAndExpand(paperId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPaperDto)
        );

        // then
        actions
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("초대 요청 수락 테스트")
    void testAcceptInvitation() throws Exception {
        // given
        Long invitationId = 1L;

        doNothing().when(invitationService).acceptInvitation(invitationId);
        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/invitations/{invitation-id}/accept")
                .buildAndExpand(invitationId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.post(uri));

        // then
        actions
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("초대 요청 거절 테스트")
    void testRejectInvitation() throws Exception {
        // given
        Long invitationId = 1L;

        doNothing().when(invitationService).rejectInvitation(invitationId);
        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/invitations/{invitation-id}/reject")
                .buildAndExpand(invitationId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.post(uri));

        // then
        actions
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 초대 요청 리스트")
    void testReceivedInvitation() throws Exception {
        //given
        Long paperId = 1L;

        String senderEmail = "sender@gmail.com";
        String receiverEmail1 = "receiver1@gmail.com";
        String receiverEmail2 = "receiver2@gmail.com";

        User sender = User.builder()
                .email(senderEmail)
                .nickname("Test Nickname")
                .build();

        User receiver1 = User.builder()
                .email(receiverEmail1)
                .build();

        User receiver2 = User.builder()
                .email(receiverEmail2)
                .build();

        Paper paper = Paper.builder()
                .id(paperId)
                .user(sender)
                .title("Test Paper")
                .isPublic(IsPublic.FRIEND)
                .build();


        List<Invitation> invitations = Arrays.asList(
                new Invitation(1L, sender, receiver1, paper, InvitationStatus.ACCEPTED),
                new Invitation(2L, sender, receiver2, paper, InvitationStatus.ACCEPTED)
        );

        given(invitationService.getReceivedInvitations()).willReturn(invitations);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/invitations/received-list")
                .buildAndExpand(paperId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(invitations.get(0).getId()))
                .andExpect(jsonPath("$[0].paperId").value(invitations.get(0).getPaper().getId()))
                .andExpect(jsonPath("$[0].status").value(invitations.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].title").value(invitations.get(0).getPaper().getTitle().toString()))
                .andExpect(jsonPath("$[0].senNickname").value(invitations.get(0).getSender().getNickname().toString()))
                .andExpect(jsonPath("$[1].id").value(invitations.get(1).getId()))
                .andExpect(jsonPath("$[1].paperId").value(invitations.get(1).getPaper().getId()))
                .andExpect(jsonPath("$[1].status").value(invitations.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].title").value(invitations.get(1).getPaper().getTitle().toString()))
                .andExpect(jsonPath("$[1].senNickname").value(invitations.get(1).getSender().getNickname().toString()));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("초대장을 수락한 유저 리스트")
    void getAcceptedUsers() throws Exception {
        //given
        Long paperId = 1L;

        AuthDto.UserDto userDto = new AuthDto.UserDto();
        userDto.setNickname("testNickname");
        userDto.setCodename("TESTAB");

        User receiver1 = User.builder()
                .nickname(userDto.getNickname())
                .codename(userDto.getCodename())
                .build();

        User receiver2 = User.builder()
                .nickname(userDto.getNickname())
                .codename(userDto.getCodename())
                .build();


        List<User> users = Arrays.asList(receiver1, receiver2);

        given(invitationService.getAcceptedUsers(any(Long.class))).willReturn(users);

        URI uri = UriComponentsBuilder
                .newInstance()
                .path("/api/invitations/papers/{paper-id}/accepted-users-list")
                .buildAndExpand(paperId)
                .toUri();

        // when
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.get(uri)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nickname").value(users.get(0).getNickname()))
                .andExpect(jsonPath("$[0].codename").value(users.get(0).getCodename()))
                .andExpect(jsonPath("$[1].nickname").value(users.get(1).getNickname()))
                .andExpect(jsonPath("$[1].codename").value(users.get(1).getCodename()));

    }
}