package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageServiceTest {
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private PaperRepository paperRepository;
    @MockBean
    private UserRepository userRepository;
    @Autowired
    private MessageService messageService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 저장 테스트")
    void testSaveMessage() {
        //given
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

        //given
        MessageDto messageDto = new MessageDto();
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message = messageDto.toEntity(user, paper);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //when
        Message savedMessage = messageService.saveMessages(paperId, messageDto);

        //then
        assertEquals(savedMessage.getName(), "Test Name", "1자 이상 10자 이하로 작성해주세요.");
        assertEquals(savedMessage.getContent(), "Test Content", "1자 이상 250자 이하로 작성해주세요.");

        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 조회 테스트")
    void testGetMessage() {
        //given
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

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message = messageDto.toEntity(user, paper);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        //when
        Message getMessage = messageService.getMessage(paperId, messageId);

        //then
        assertEquals(getMessage.getId(), messageId);
        assertEquals(getMessage.getPaper().getId(), paperId);
        assertEquals(getMessage.getName(), "Test Name");
        assertEquals(getMessage.getContent(), "Test Content");

        // verify
        verify(messageRepository, times(1)).findById(messageId);
        verify(paperRepository, times(1)).findById(paperId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 수정 테스트")
    void testUpdateMessage() {
        //given
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

        Long messageId = 1L;
        MessageDto updatedMessageDto = new MessageDto();
        updatedMessageDto.setId(messageId);
        updatedMessageDto.setName("Updated Name");
        updatedMessageDto.setContent("Updated Content");

        Message message = Message.builder()
                .id(1L)
                .user(user)
                .paper(paper)
                .name(updatedMessageDto.getName())
                .content(updatedMessageDto.getContent())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //when
        Message updatedMessage = messageService.updateMessage(paperId, messageId, updatedMessageDto);

        //then
        assertEquals(updatedMessage.getId(), messageId);
        assertEquals(updatedMessage.getPaper().getId(), paperId);
        assertEquals(updatedMessage.getName(), "Updated Name");
        assertEquals(updatedMessage.getContent(), "Updated Content");

        verify(messageRepository, times(1)).findById(messageId);
        verify(paperRepository, times(1)).findById(paperId);
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("특정 롤링페이퍼의 전체 메시지 조회")
    void testGetMessages() {
        //given
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

        Long messageId = 1L;
        MessageDto updatedMessageDto = new MessageDto();
        updatedMessageDto.setId(messageId);
        updatedMessageDto.setName("Test Name");
        updatedMessageDto.setContent("Test Content");

        Message message1 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(updatedMessageDto.getName())
                .content(updatedMessageDto.getContent())
                .build();

        Message message2 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(updatedMessageDto.getName())
                .content(updatedMessageDto.getContent())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.findByPaper(paper)).thenReturn(Arrays.asList(message1, message2));

        // When
        List<Message> messages = messageService.getMessages(paperId);

        // Then
        assertEquals(messages.size(), 2);

        verify(paperRepository, times(1)).findById(paperId);
        verify(messageRepository, times(1)).findByPaper(paper);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("내가 작성한 public 메시지 전체 조회")
    void testMyPublicMessages() {
        //given
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

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message1 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(messageDto.getName())
                .content(messageDto.getContent())
                .build();

        Message message2 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(messageDto.getName())
                .content(messageDto.getContent())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(paperRepository.findByIsPublic(IsPublic.PUBLIC)).thenReturn(Arrays.asList(paper));
        when(messageRepository.findByPaperAndUser(paper, user)).thenReturn(Arrays.asList(message1, message2));

        // When
        List<Message> messages = messageService.getMyPublicMessages();

        // Then
        assertEquals(messages.size(), 2);

        verify(paperRepository, times(1)).findByIsPublic(IsPublic.PUBLIC);
        verify(messageRepository, times(1)).findByPaperAndUser(paper, user);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("내가 작성한 friend 메시지 전체 조회")
    void testMyFriendMessages() {
        //given
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
        paperDto.setIsPublic(IsPublic.FRIEND);

        Paper paper = paperDto.toEntity(user);

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message1 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(messageDto.getName())
                .content(messageDto.getContent())
                .build();

        Message message2 = Message.builder()
                .id(1L)
                .paper(paper)
                .name(messageDto.getName())
                .content(messageDto.getContent())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(paperRepository.findByIsPublic(IsPublic.FRIEND)).thenReturn(Arrays.asList(paper));
        when(messageRepository.findByPaperAndUser(paper, user)).thenReturn(Arrays.asList(message1, message2));

        // When
        List<Message> messages = messageService.getMyFriendMessages();

        // Then
        assertEquals(messages.size(), 2);

        verify(paperRepository, times(1)).findByIsPublic(IsPublic.FRIEND);
        verify(messageRepository, times(1)).findByPaperAndUser(paper, user);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("메시지 삭제")
    void testDeleteMessage() {
        //given
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

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setName("Updated Name");
        messageDto.setContent("Updated Content");

        Message message = Message.builder()
                .id(1L)
                .user(user)
                .paper(paper)
                .name(messageDto.getName())
                .content(messageDto.getContent())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        //when
        messageService.deleteMessage(paperId, messageId);

        //verify
        verify(messageRepository, times(1)).findById(messageId);
        verify(paperRepository, times(1)).findById(paperId);
        verify(messageRepository, times(1)).delete(message);
    }
}