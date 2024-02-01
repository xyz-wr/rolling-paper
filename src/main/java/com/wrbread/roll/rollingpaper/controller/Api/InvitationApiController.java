package com.wrbread.roll.rollingpaper.controller.Api;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationApiController {
    private final InvitationService invitationService;

    /** 초대장 발송 */
    @PostMapping("papers/{paper-id}/invite")
    public ResponseEntity<InvitationDto.Response> sendInvitation(@PathVariable("paper-id") Long paperId,
                                                                 @RequestBody InvitationDto.Request request){
        Invitation invitation = invitationService.invite(paperId, request);

        return ResponseEntity.ok().body(new InvitationDto.Response(invitation));
    }

    /** 초대 요청 수락 */
    @PostMapping("/{invitation-id}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable("invitation-id") Long invitationId) {
        invitationService.acceptInvitation(invitationId);

        return ResponseEntity.ok().build();
    }

    /** 초대 요청 거절 */
    @PostMapping("/{invitation-id}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable("invitation-id") Long invitationId) {
        invitationService.rejectInvitation(invitationId);

        return ResponseEntity.ok().build();
    }

    /** 참여 요청 받은 롤링 페이퍼의 초대장 전체 리스트 */
    @GetMapping("/received-list")
    public ResponseEntity<List<InvitationDto.Response>> receivedInvitations() {
        List<InvitationDto.Response> responseDtos = invitationService.getReceivedInvitations()
                .stream()
                .map(InvitationDto.Response::new)
                .toList();

        return ResponseEntity.ok(responseDtos);
    }

    /** 초대장을 수락한 유저 리스트 */
    @GetMapping("/papers/{paper-id}/accepted-users-list")
    public ResponseEntity<List<AuthDto.UserDto>> AcceptedUsers(@PathVariable("paper-id") Long paperId){
        List<AuthDto.UserDto> responseDtos = invitationService.getAcceptedUsers(paperId)
                .stream()
                .map(AuthDto.UserDto::new)
                .toList();

        return ResponseEntity.ok(responseDtos);
    }

    /** 롤링 페이퍼 탈퇴 */
    @DeleteMapping("/papers/{paper-id}/withdraw")
    public ResponseEntity<Void> withdrawInvitation(@PathVariable("paper-id") Long paperId) {

        invitationService.withdrawInvitation(paperId);
        return ResponseEntity.noContent().build();
    }
}
