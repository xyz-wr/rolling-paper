package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    private final PaperRepository paperRepository;

    /** 롤링 페이퍼 아이디 및 메시지 아이디 조회 */
    public Message findByPaperIdAndMessageId(Long paperId, Long messageId) {
        return messageRepository.findByPaperIdAndId(paperId, messageId);
    }

    /** 메시지 등록 */
    public Message saveMessages(Long paperId, MessageDto messageDto)   {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("해당 롤링 페이퍼가 존재하지 않습니다."));

        Message message = messageDto.toEntity(paper);

        return messageRepository.save(message);
    }

    /** 메시지 수정 */
    public Message updateMessage(Long paperId, Long messageId, MessageDto messageDto) {
        Message message = findByPaperIdAndMessageId(paperId, messageId);
        message.updateMessage(messageDto);

        return messageRepository.save(message);
    }

    /** 메시지 조회 */
    public Message getMessage(Long paperId, Long messageId) {
        Message message = findByPaperIdAndMessageId(paperId, messageId);

        return message;
    }

    /** 특정 롤링페이퍼의 전체 메시지 조회 */
    public List<Message> getMessages(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new IllegalArgumentException("해당 롤링 페이퍼가 존재하지 않습니다."));

        return messageRepository.findByPaper(paper);
    }

    /** 메시지 삭제 */
    public void deleteMessage(Long paperId, Long messageId) {
        Message message = findByPaperIdAndMessageId(paperId, messageId);

        messageRepository.delete(message);
    }

}