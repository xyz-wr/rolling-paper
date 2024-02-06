package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.InvitationRepository;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
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
    private MessageRepository messageRepository;

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

        User sender = User.userDetail()
                .email(senEmail)
                .build();

        InvitationDto.Request request = new InvitationDto.Request();
        request.setRecEmail("receiver@gmail.com");

        String recEmail = request.getRecEmail();
        User receiver = User.userDetail()
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

        User receiver = User.userDetail()
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

        User receiver = User.userDetail()
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
    @DisplayName("withdrawInvitation 테스트")
    public void testWithdrawInvitation() {
        //given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String recEmail = authentication.getName();

        User receiver = User.userDetail()
                .email(recEmail)
                .build();

        Paper paper = Paper.builder()
                .id(1L)
                .title("Test Paper")
                .isPublic(IsPublic.FRIEND)
                .build();

        Message message1 = Message.builder()
                .id(1L)
                .user(receiver)
                .paper(paper)
                .name("testMessage")
                .content("testContent")
                .build();

        Message message2 = Message.builder()
                .id(2L)
                .user(receiver)
                .paper(paper)
                .name("testMessage")
                .content("testContent")
                .build();

        Long invitationId = 1L;
        Invitation invitation = Invitation.builder()
                .id(invitationId)
                .paper(paper)
                .status(InvitationStatus.ACCEPTED)
                .receiver(receiver)
                .build();

        when(userRepository.findByEmail(recEmail)).thenReturn(java.util.Optional.of(receiver));
        when(paperRepository.findById(1L)).thenReturn(Optional.of(paper));
        when(invitationRepository.findByPaperAndReceiverAndStatus(paper, receiver, InvitationStatus.ACCEPTED)).thenReturn(Optional.of(invitation));
        when(messageRepository.findByPaperAndUser(paper, receiver)).thenReturn(Arrays.asList(message1, message2));

        // when
        invitationService.withdrawInvitation(invitationId);

        // then
        assertEquals(InvitationStatus.WITHDRAW, invitation.getStatus());
        verify(invitationRepository, times(1)).save(invitation);
        verify(messageRepository, times(1)).deleteAll(Arrays.asList(message1, message2));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("getReceivedInvitations 테스트")
    public void testGetReceivedInvitations() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String recEmail = authentication.getName();

        User receiver = User.userDetail()
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
        User user = User.userDetail()
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

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("rejectInvitation 테스트")
    public void testDeleteInvitationsForPaper() {
        // 테스트에 필요한 데이터 설정
        Paper paper = new Paper();
        Invitation invitation1 = new Invitation();
        Invitation invitation2 = new Invitation();
        List<Invitation> invitations = Arrays.asList(invitation1, invitation2);

        when(invitationRepository.findByPaper(paper)).thenReturn(invitations);

        invitationService.deleteInvitationsForPaper(paper);

        verify(invitationRepository, times(1)).delete(invitation1);
        verify(invitationRepository, times(1)).delete(invitation2);
    }

}