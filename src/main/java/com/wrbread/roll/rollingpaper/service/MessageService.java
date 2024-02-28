package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Like;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.LikeRepository;
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

    private final LikeRepository likeRepository;


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

        //유효성 검사
        validateMessageDto(messageDto);

        Message message = messageDto.toEntity(user, paper);

        return messageRepository.save(message);
    }


    /** 메시지 수정 시 조회
     * 메시지 작성자만 조회 가능*/
    public MessageDto getEditMessage(Long paperId, Long messageId) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());

        checkWriter(user, message);

        MessageDto messageDto = new MessageDto(message);

        return messageDto;

    }

    /** 메시지 수정
     * 메시지 작성자만 수정 가능
     * */
    public Message updateMessage(Long paperId, Long messageId, MessageDto messageDto) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());

        checkWriter(user, message);

        // 유효성 검사
        validateMessageDto(messageDto);

        message.updateMessage(messageDto);

        return messageRepository.save(message);
    }

    /** 메시지 조회
     * 친구 초대 롤링 페이퍼 작성자와 초대장을 수락한 유저만 해당 페이지 조회 가능
     * */
    public Message getMessage(Long paperId, Long messageId) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());

        return message;
    }

    /** 특정 롤링페이퍼의 전체 메시지 조회
     * 친구 초대 롤링 페이퍼 작성자와 초대장을 수락한 유저만 롤링 페이퍼의 전체 페이지 조회 가능
     **/
    public List<Message> getMessages(Long paperId) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        invitationService.checkOwnerAndAccepted(user, paper);

        return messageRepository.findByPaper(paper);
    }

    /** 내가 작성한 public 메시지 전체 조회 */
    public List<Message> getMyPublicMessages() {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findByIsPublic(IsPublic.PUBLIC);

        List<Message> messages = papers.stream()
                .flatMap(paper -> messageRepository.findByPaperAndUser(paper, user).stream())
                .toList();

        return messages;
    }


    /** 내가 작성한 friend 메시지 전체 조회 */
    public List<Message> getMyFriendMessages() {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findByIsPublic(IsPublic.FRIEND);

        List<Message> messages = papers.stream()
                .flatMap(paper -> messageRepository.findByPaperAndUser(paper, user).stream())
                .toList();

        return messages;
    }

    /** 메시지 삭제
     * 메시지 작성자만 삭제 가능
     * */
    public void deleteMessage(Long paperId, Long messageId) {
        User user = userService.verifiedEmail();

        Message message = findByPaperIdAndMessageId(paperId, messageId);

        invitationService.checkOwnerAndAccepted(user, message.getPaper());


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

    /** 내가 좋아요 누른 메시지 목록 */
    public List<Message> getMyLikes() {
        User user = userService.verifiedEmail();

        List<Like> likes = likeRepository.findAllByUser(user);
        List<Message> likedMessages = likes.stream()
                .map(Like::getMessage)
                .toList();

        return likedMessages;
    }

    /** 메시지 유효성 검사 */
    private void validateMessageDto(MessageDto messageDto) {
        if (messageDto.getName() == null || messageDto.getName().isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.MESSAGE_NAME_REQUIRED);
        }

        if (messageDto.getName().length() < 1 || messageDto.getName().length() > 10) {
            throw new BusinessLogicException(ExceptionCode.INVALID_NAME_LENGTH);
        }

        if (messageDto.getContent().isEmpty() || messageDto.getContent() == null) {
            throw new BusinessLogicException(ExceptionCode.MESSAGE_CONTENT_REQUIRED);
        }

        if (messageDto.getContent().length() < 1 || messageDto.getContent().length() > 250) {
            throw new BusinessLogicException(ExceptionCode.INVALID_CONTENT_LENGTH);
        }
    }
}