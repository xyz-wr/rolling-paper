package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PaperRepository paperRepository;


    @Test
    @DisplayName("findByPaperIdAndId 테스트")
    public void testFindByPaperIdAndId() {
        // given
        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);
        Long paperId = paper.getId();

        Message message = Message.builder()
                .paper(paper)
                .name("name")
                .content("content")
                .build();

        messageRepository.save(message);
        Long messageId= message.getId();

        // when
        Message result = messageRepository.findByPaperIdAndId(paperId, messageId);

        // then
        assertNotNull(result);
        assertEquals(paperId, result.getPaper().getId());
        assertEquals(messageId, result.getId());
    }

    @Test
    @DisplayName("findByPaper 테스트")
    public void testFindByPaper() {
        // given
        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);
        Long paperId = paper.getId();

        MessageDto messageDto1 = new MessageDto();
        messageDto1.setPaperId(paperId);
        messageDto1.setName("name1");
        messageDto1.setContent("content1");

        MessageDto messageDto2 = new MessageDto();
        messageDto2.setPaperId(paperId);
        messageDto2.setName("name2");
        messageDto2.setContent("content2");

        Message message1 = Message.builder()
                .paper(paper)
                .id(messageDto1.getId())
                .name(messageDto1.getName())
                .content(messageDto1.getContent()).build();

        Message message2 = Message.builder()
                .paper(paper)
                .id(messageDto2.getId())
                .name(messageDto2.getName())
                .content(messageDto2.getContent()).build();
//        Message message2 = new Message(paper, messageDto2.getId(), messageDto2.getName(), messageDto2.getContent());


        List<Message> messages = Arrays.asList(message1, message2);
        messageRepository.saveAll(messages);

        // when
        List<Message> result = messageRepository.findByPaper(paper);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(message1));
        assertTrue(result.contains(message2));
    }
}