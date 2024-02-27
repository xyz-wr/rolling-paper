package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.service.LikeService;
import com.wrbread.roll.rollingpaper.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    private final LikeService likeService;

    /** 메시지 조회 */
    @GetMapping("/papers/{paper-id}/messages/{message-id}")
    public String detail(@PathVariable("paper-id") Long paperId,
                         @PathVariable("message-id") Long messageId,
                         Model model, Authentication auth) {
        Message message = messageService.getMessage(paperId, messageId);
        MessageDto messageDto = new MessageDto(message);


        model.addAttribute("email", auth.getName());
        model.addAttribute("messageDto", messageDto);
        model.addAttribute("paperId", paperId);
        model.addAttribute("messageId", messageId);
        model.addAttribute("messageUser", messageDto.getEmail());
        model.addAttribute("likeCount", messageDto.getLikeCount());

        return "message/detail";
    }


    /** 메시지 등록 화면 로딩 */
    @GetMapping("/papers/{paper-id}/messages/write")
    public String post(@PathVariable("paper-id") Long paperId,
                       Model model) {
        model.addAttribute("paperId", paperId);
        model.addAttribute("messageDto", new MessageDto());

        return "message/write";
    }

    /** 메시지 등록 */
    @PostMapping("/papers/{paper-id}/messages/write")
    public String write(@PathVariable("paper-id") Long paperId,
                        MessageDto messageDto, Model model,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "message/write";
        }

        try {
            messageService.saveMessages(paperId, messageDto);
        } catch (BusinessLogicException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("paperId", paperId);
            return "message/write";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "메시지 등록 중 에러가 발생하였습니다.");
            return "message/write";
        }

        return "redirect:/papers/{paper-id}";
    }

    /** 메시지 수정 페이지 로딩 */
    @GetMapping("/papers/{paper-id}/messages/edit/{message-id}")
    public String update(@PathVariable("paper-id") Long paperId,
                         @PathVariable("message-id") Long messageId,
                         Model model) {
        MessageDto messageDto = messageService.getEditMessage(paperId, messageId);
        model.addAttribute("messageDto", messageDto);
        model.addAttribute("paperId", paperId);

        return "message/write";
    }

    /** 메시지 수정 */
    @PostMapping("/papers/{paper-id}/messages/edit/{message-id}")
    public String edit(@PathVariable("paper-id") Long paperId,
                       @PathVariable("message-id") Long messageId,
                       MessageDto messageDto, Model model,
                       BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "message/write";
        }

        try {
            messageService.updateMessage(paperId, messageId, messageDto);
        } catch (BusinessLogicException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("paperId", paperId);
            return "message/write";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "메시지 수정 중 에러가 발생하였습니다.");
            return "message/write";
        }

        return "redirect:/papers/{paper-id}";
    }

    /** 메시지 삭제 */
    @GetMapping("/papers/{paper-id}/messages/delete/{message-id}")
    public String delete(@PathVariable("paper-id") Long paperId,
                         @PathVariable("message-id") Long messageId) {

        messageService.deleteMessage(paperId, messageId);

        return "redirect:/papers/{paper-id}";
    }

    /** 내가 작성한 public 롤링페이퍼의 메시지 전체 조회 */
    @GetMapping("/messages/my-public-message-list")
    public String myPublicList(Model model) {
        List<MessageDto> messageDtos = messageService.getMyPublicMessages()
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("messageDtos", messageDtos);

        return "message/my-public-messages";
    }

    /** 내가 작성한 friend 롤링 페이퍼의 메시지 전체 조회 */
    @GetMapping("/messages/my-friend-message-list")
    public String myFriendList(Model model) {
        List<MessageDto> messageDtos = messageService.getMyFriendMessages()
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("messageDtos", messageDtos);

        return "message/my-friend-messages";
    }


    /** 내가 좋아요 누른 메시지 전체 조회 */
    @GetMapping("/messages/like/my-like-message-list")
    public String myLikeList(Model model) {
        List<MessageDto> messageDtos = messageService.getMyLikes()
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("messageDtos", messageDtos);

        return "message/my-like-message";
    }
}
