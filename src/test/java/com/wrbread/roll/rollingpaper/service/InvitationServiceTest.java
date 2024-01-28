package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.InvitationRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class InvitationServiceTest {

    @MockBean
    private PaperRepository paperRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InvitationRepository invitationRepository;

    @Autowired
    private InvitationService invitationService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("invite 테스트")
    public void testInvite() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String senEmail = authentication.getName();

        User sender = User.builder()
                .email(senEmail)
                .build();

        InvitationDto.Request request = new InvitationDto.Request();
        request.setRecEmail("receiver@gmail.com");

        String recEmail = request.getRecEmail();
        User receiver = User.builder()
                .email(recEmail)
                .build();

        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.FRIEND);

        Paper paper = paperDto.toEntity(sender);

        when(userRepository.save(any(User.class))).thenReturn(sender);
        when(userRepository.save(any(User.class))).thenReturn(receiver);
        when(paperRepository.findById(paperId)).thenReturn(java.util.Optional.of(paper));
        when(userRepository.findByEmail(senEmail)).thenReturn(java.util.Optional.of(sender));
        when(userRepository.findByEmail(recEmail)).thenReturn(java.util.Optional.of(receiver));
        when(invitationRepository.findByPaperAndReceiver(paper, receiver)).thenReturn(null);
        when(invitationRepository.save(any(Invitation.class))).thenReturn(new Invitation());

        // when
        Invitation sendInvitation = invitationService.invite(paperId, request);

        // then
        assertNotNull(sendInvitation);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("acceptInvitation 테스트")
    public void testAcceptInvitation() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String recEmail = authentication.getName();

        User receiver = User.builder()
                .email(recEmail)
                .build();

        Long invitationId = 1L;
        Invitation invitation = Invitation.builder()
                .id(invitationId)
                .status(InvitationStatus.PENDING)
                .receiver(receiver)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(invitation));
        when(userRepository.findByEmail(recEmail)).thenReturn(java.util.Optional.of(receiver));

        // when
        invitationService.acceptInvitation(invitationId);

        // then
        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("rejectInvitation 테스트")
    public void testRejectInvitation() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String recEmail = authentication.getName();

        User receiver = User.builder()
                .email(recEmail)
                .build();

        Long invitationId = 1L;
        Invitation invitation = Invitation.builder()
                .id(invitationId)
                .status(InvitationStatus.PENDING)
                .receiver(receiver)
                .build();

        when(invitationRepository.findById(invitationId)).thenReturn(java.util.Optional.of(invitation));
        when(userRepository.findByEmail(recEmail)).thenReturn(java.util.Optional.of(receiver));

        // when
        invitationService.rejectInvitation(invitationId);

        // then
        assertEquals(InvitationStatus.REJECTED, invitation.getStatus());
        verify(invitationRepository, times(1)).save(invitation);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("getReceivedInvitations 테스트")
    public void testGetReceivedInvitations() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String recEmail = authentication.getName();

        User receiver = User.builder()
                .email(recEmail)
                .build();

        List<Invitation> invitations = new ArrayList<>(); // 초대장 목록 생성
        invitations.add(new Invitation());
        invitations.add(new Invitation());

        when(userRepository.findByEmail(recEmail)).thenReturn(java.util.Optional.of(receiver));
        when(invitationRepository.findByReceiver(receiver)).thenReturn(invitations);

        // when
        List<Invitation> receivedInvitations = invitationService.getReceivedInvitations();

        // then
        assertNotNull(receivedInvitations);
        assertEquals(2, receivedInvitations.size());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("getAcceptedUsers 테스트")
    public void testGetAcceptedUsers() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity(user);

        List<Invitation> acceptedInvitations = new ArrayList<>(); // 수락된 초대장 목록 생성
        acceptedInvitations.add(new Invitation());
        acceptedInvitations.add(new Invitation());

        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(paperRepository.findById(paperId)).thenReturn(java.util.Optional.of(paper));
        when(invitationRepository.findByPaperAndStatus(paper, InvitationStatus.ACCEPTED)).thenReturn(acceptedInvitations);

        // when
        List<User> acceptedUsers = invitationService.getAcceptedUsers(paperId);

        // then
        assertNotNull(acceptedUsers);
        assertEquals(2, acceptedUsers.size());
    }

}