package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class MessageServiceTest {
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private PaperRepository paperRepository;

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("메시지 저장")
    void testSaveMessage() {

        //given
        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity();

        //given
        MessageDto messageDto = new MessageDto();
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message = messageDto.toEntity(paper);

        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //when
        Message savedMessage = messageService.saveMessages(paperId, messageDto);

        //then
        Assertions.assertEquals(savedMessage.getName(), "Test Name", "1자 이상 10자 이하로 작성해주세요.");
        Assertions.assertEquals(savedMessage.getContent(), "Test Content", "1자 이상 250자 이하로 작성해주세요.");

        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("메시지 조회")
    void testGetMessage() {
        //given
        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity();

        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setId(messageId);
        messageDto.setName("Test Name");
        messageDto.setContent("Test Content");

        Message message = messageDto.toEntity(paper);

        when(messageRepository.findByPaperIdAndId(paperId, messageId)).thenReturn(message);

        //when
        Message getMessage = messageService.getMessage(paperId, messageId);

        //then
        Assertions.assertEquals(getMessage.getId(), messageId);
        Assertions.assertEquals(getMessage.getPaper().getId(), paperId);
        Assertions.assertEquals(getMessage.getName(), "Test Name");
        Assertions.assertEquals(getMessage.getContent(), "Test Content");

        // verify
        verify(messageRepository, times(1)).findByPaperIdAndId(paperId, messageId);
    }

    @Test
    @DisplayName("메시지 수정")
    void testUpdateMessage() {
        //given
        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity();

        Long messageId = 1L;
        MessageDto updatedMessageDto = new MessageDto();
        updatedMessageDto.setId(messageId);
        updatedMessageDto.setName("Updated Name");
        updatedMessageDto.setContent("Updated Content");

        Message message = Message.builder()
                .id(1L)
                .paper(paper)
                .name(updatedMessageDto.getName())
                .content(updatedMessageDto.getContent())
                .build();

        when(messageRepository.findByPaperIdAndId(paperId, messageId)).thenReturn(message);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        //when
        Message updatedMessage = messageService.updateMessage(paperId, messageId, updatedMessageDto);

        //then
        Assertions.assertEquals(updatedMessage.getId(), messageId);
        Assertions.assertEquals(updatedMessage.getPaper().getId(), paperId);
        Assertions.assertEquals(updatedMessage.getName(), "Updated Name");
        Assertions.assertEquals(updatedMessage.getContent(), "Updated Content");

        verify(messageRepository, times(1)).findByPaperIdAndId(paperId, messageId);
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("특정 롤링페이퍼의 전체 메시지 조회")
    void testGetMessages() {
        //given
        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity();

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

        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(messageRepository.findByPaper(paper)).thenReturn(Arrays.asList(message1, message2));

        // When
        List<Message> messages = messageService.getMessages(paperId);

        // Then
        Assertions.assertEquals(messages.size(), 2);

        verify(paperRepository, times(1)).findById(paperId);
        verify(messageRepository, times(1)).findByPaper(paper);

    }

    @Test
    @DisplayName("메시지 삭제")
    void testDeleteMessage() {
        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity();

        //given
        Long messageId = 1L;
        MessageDto updatedMessageDto = new MessageDto();
        updatedMessageDto.setId(messageId);
        updatedMessageDto.setName("Updated Name");
        updatedMessageDto.setContent("Updated Content");

        Message message = Message.builder()
                .id(1L)
                .paper(paper)
                .name(updatedMessageDto.getName())
                .content(updatedMessageDto.getContent())
                .build();

        when(messageRepository.findByPaperIdAndId(paperId, messageId)).thenReturn(message);

        //when
        messageService.deleteMessage(paperId, messageId);

        //verify
        verify(messageRepository, times(1)).findByPaperIdAndId(paperId, messageId);
        verify(messageRepository, times(1)).delete(message);
    }
}