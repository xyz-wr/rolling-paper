package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
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
    @PostMapping("papers/{paper-id}/messages/write")
    public String write(@PathVariable("paper-id") Long paperId,
                        MessageDto messageDto, Model model) {

        if (messageDto.getName() == null || messageDto.getName().isEmpty()) {
            model.addAttribute("errorMessage", "이름을 입력해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        if (messageDto.getName().length() < 1 || messageDto.getName().length() > 10) {
            model.addAttribute("errorMessage","이름은 1자 이상 10자 이하로 작성해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        if (messageDto.getContent().isEmpty() || messageDto.getContent() == null) {
            model.addAttribute("errorMessage", "내용을 입력해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }


        if (messageDto.getContent().length() < 1 || messageDto.getContent().length() > 250) {
            model.addAttribute("errorMessage","내용은 1자 이상 250자 이하로 작성해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        try {
            messageService.saveMessages(paperId, messageDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "롤링페이퍼 등록 중 에러가 발생하였습니다.");
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


        if (messageDto.getName() == null || messageDto.getName().isEmpty()) {
            model.addAttribute("errorMessage", "이름을 입력해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        if (messageDto.getName().length() < 1 || messageDto.getName().length() > 10) {
            model.addAttribute("errorMessage","이름은 1자 이상 10자 이하로 작성해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        if (messageDto.getContent().isEmpty() || messageDto.getContent() == null) {
            model.addAttribute("errorMessage", "내용을 입력해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }


        if (messageDto.getContent().length() < 1 || messageDto.getContent().length() > 250) {
            model.addAttribute("errorMessage","내용은 1자 이상 250자 이하로 작성해주세요.");
            model.addAttribute("paperId", paperId);
            return "message/write";
        }

        try {
            messageService.updateMessage(paperId, messageId, messageDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "롤링페이퍼 수정 중 에러가 발생하였습니다.");
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

    /** 내가 작성한 public 롤링 페이퍼 전체 조회 */
    @GetMapping("/messages/my-public-message-list")
    public String myPublicList(Model model) {
        List<MessageDto> messageDtos = messageService.getMyPublicMessages()
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("messageDtos", messageDtos);

        return "message/my-public-messages";
    }

    /** 내가 작성한 friend 롤링 페이퍼 전체 조회 */
    @GetMapping("/messages/my-friend-message-list")
    public String myFriendList(Model model) {
        List<MessageDto> messageDtos = messageService.getMyFriendMessages()
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("messageDtos", messageDtos);

        return "message/my-friend-messages";
    }

}
