package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InvitationController {
    private final InvitationService invitationService;

    /** 롤링 페이퍼 초대 요청 */
    @PostMapping("papers/{paper-id}/invite")
    public String sendInvitation(@PathVariable("paper-id") Long paperId,
                                 @Valid InvitationDto.Request request) {

        invitationService.invite(paperId, request);

        return "redirect:/papers/{paper-id}/search/codename";
    }

    /** 롤링 페이퍼 초대 요청 리스트 */
    @GetMapping("/invitations/received")
    public String getInvitations(Model model) {
        List<InvitationDto.Response> invitationDtos = invitationService.getReceivedInvitations()
                .stream()
                .map(InvitationDto.Response::new)
                .toList();

        model.addAttribute("invitationDtos", invitationDtos);
        return "invitation/list";
    }

    /** 롤링 페이퍼 초대 요청 수락 */
    @PostMapping("invitations/{invitation-id}/accept")
    public String acceptInvitation(@PathVariable("invitation-id") Long invitationId) {
        invitationService.acceptInvitation(invitationId);

        return "redirect:/invitations/received";
    }

    /** 롤링 페이퍼 초대 요청 거절 */
    @PostMapping("invitations/{invitation-id}/reject")
    public String rejectInvitation(@PathVariable("invitation-id") Long invitationId) {
        invitationService.rejectInvitation(invitationId);

        return "redirect:/invitations/received";
    }

    /** 롤링 페이퍼 초대 수락 철회 */
    @PostMapping("/papers/{paper-id}/invitations/withdraw")
    public String withdrawInvitation(@PathVariable("paper-id") Long paperId) {
        invitationService.withdrawInvitation(paperId);

        return "redirect:/papers/all-public-papers";
    }

    /** 초대장을 수락한 유저 리스트 */
    @GetMapping("/papers/{paper-id}/invitations/accepted")
    public String getAcceptedReceiver(@PathVariable("paper-id") Long paperId,
                                      Model model) {
        List<AuthDto.UserDto> acceptedDtos = invitationService.getAcceptedUsers(paperId)
                .stream()
                .map(AuthDto.UserDto::new)
                .toList();

        model.addAttribute("paperId", paperId);
        model.addAttribute("acceptedDtos", acceptedDtos);
        return "invitation/acceptedList";
    }
}
