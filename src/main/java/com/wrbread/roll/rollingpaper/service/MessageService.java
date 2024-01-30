package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    private final PaperRepository paperRepository;

    private final UserService userService;

    private final InvitationService invitationService;


    /** 롤링 페이퍼 아이디 및 메시지 아이디 조회 */
    public Message findByPaperIdAndMessageId(Long paperId, Long messageId) {
        Optional<Paper> paper = paperRepository.findById(paperId);
        if (paper.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND);
        }

        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND);
        }

        return message.get();
    }

    /** 메시지 등록
     * 롤링 페이퍼 작성자와 초대장을 수락한 유저만 작성 가능
     * */
    public Message saveMessages(Long paperId, MessageDto messageDto)   {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        invitationService.checkOwnerAndAccepted(user, paper);

        Message message = messageDto.toEntity(user, paper);

        return messageRepository.save(message);
    }

    /** 메시지 수정
     * 메시지 작성자만 수정 가능
     * */
    public Message updateMessage(Long paperId, Long messageId, MessageDto messageDto) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());
        checkWriter(user, message);

        message.updateMessage(messageDto);

        return messageRepository.save(message);
    }

    /** 메시지 조회
     * 롤링 페이퍼 작성자와 초대장을 수락한 유저만 해당 페이지 조회 가능
     * */
    public Message getMessage(Long paperId, Long messageId) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());

        return message;
    }

    /** 특정 롤링페이퍼의 전체 메시지 조회
     * 롤링 페이퍼 작성자와 초대장을 수락한 유저만 롤링 페이퍼의 전체 페이지 조회 가능
     **/
    public List<Message> getMessages(Long paperId) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        if (paper.getIsPublic().equals(IsPublic.FRIEND)) {
            invitationService.checkOwnerAndAccepted(user, paper);
        }

        return messageRepository.findByPaper(paper);
    }

    /** 메시지 삭제
     * 메시지 작성자만 삭제 가능
     * */
    public void deleteMessage(Long paperId, Long messageId) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        checkWriter(user, message);

        messageRepository.delete(message);
    }


    /** 메시지를 작성한 유저인지 확인 */
    public void checkWriter(User user, Message message) {
        boolean isWriter = message.getUser().getId().equals(user.getId());
        if (!isWriter) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_ACCESS);
        }
    }

}