package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.InvitationDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.InvitationRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InvitationService {
    private final UserRepository userRepository;

    private final PaperRepository paperRepository;

    private final InvitationRepository invitationRepository;

    /** 다이어리 초대 요청
     * 자기 자신은 초대 불가
     * 전체 공개는 초대장 발송 불가
     * 해당 롤링 페이퍼를 작성한 유저만 초대장 발송 가능
     * 이미 초대장을 수락한 경우 초대 불가
     */
    public Invitation invite(Long paperId, InvitationDto.Request request) {
        String senEmail = SecurityUtil.getCurrentUsername(); //발신자
        String recEmail = request.getRecEmail();

        if (senEmail.equals(recEmail)) {
            throw new IllegalArgumentException("자기 자신은 초대할 수 없습니다.");
        }

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(EntityNotFoundException::new);
        User sender = userRepository.findByEmail(senEmail)
                .orElseThrow(EntityNotFoundException::new);
        User receiver = userRepository.findByEmail(recEmail)
                .orElseThrow(EntityNotFoundException::new);

        // paper의 isPublic이 FRIEND이어야 초대장 발송 가능
        if (paper.getIsPublic().equals(IsPublic.PUBLIC)) {
            throw new IllegalArgumentException("전체 공개는 초대장을 보낼 수 없습니다.");
        }

        // 해당 롤링 페이퍼를 작성한 유저만 초대장 발송 가능
        if (paper.getUser() != sender) {
            throw new IllegalArgumentException("롤링 페이퍼를 생성한 유저만 초대장 발송이 가능합니다.");
        }

        // receiver의 현재 초대장 상태 확인
        Invitation acceptedInvitation = invitationRepository.findByPaperAndReceiver(paper, receiver);

        // 이미 초대장을 수락한 경우 초대 불가
        if (acceptedInvitation != null && acceptedInvitation.getStatus().equals(InvitationStatus.ACCEPTED)){
            throw new IllegalArgumentException("이미 초대를 허락했습니다.");
        }

        Invitation invitation = request.toEntity(sender, receiver, paper, InvitationStatus.PENDING);
        return invitationRepository.save(invitation);
    }

    /** 롤링 페이퍼 초대 요청 수락
     * 이미 수락 및 거절된 초대 요청은 수락 불가
     * */
    public void acceptInvitation(Long invitationId) {
        Invitation invitation = findInvitation(invitationId);

        String recEmail = SecurityUtil.getCurrentUsername();

        User receiver = userRepository.findByEmail(recEmail)
                .orElseThrow(EntityNotFoundException::new);

        // 초대장을 받은 유저인지 확인
        if (!invitation.getReceiver().getEmail().equals(receiver.getEmail())){
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        }

        // 이미 수락 및 거절된 초대 요청은 수락 불가
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("이미 수락하거나 거절한 초대장입니다.");
        }

        invitation.accept();

        invitationRepository.save(invitation);
    }

    /** 롤링 페이퍼 초대 요청 거절
     * 이미 수락 및 거절된 초대 요청은 거절 불가
     * */
    public void rejectInvitation(Long invitationId) {
        Invitation invitation = findInvitation(invitationId);

        String recEmail = SecurityUtil.getCurrentUsername();

        User receiver = userRepository.findByEmail(recEmail)
                .orElseThrow(EntityNotFoundException::new);

        // 초대장을 받은 유저인지 확인
        if(!invitation.getReceiver().getEmail().equals(receiver.getEmail())) {
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        }

        // 이미 수락 및 거절된 초대 요청은 수락 불가
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("이미 수락하거나 거절한 초대입니다.");
        }

        invitation.reject();
        invitationRepository.save(invitation);
    }

    /** 참여 요청 받은 롤링 페이퍼의 초대장 전체 리스트 */
    public List<Invitation> getReceivedInvitations() {
        String recEmail = SecurityUtil.getCurrentUsername();

        User receiver= userRepository.findByEmail(recEmail)
                .orElseThrow(EntityNotFoundException::new);

        return invitationRepository.findByReceiver(receiver);
    }

    /** 초대장을 수락한 유저 리스트
     * 롤링페이퍼 작성자와 초대장을 수락한 유저만 조회 가능 */
    public List<User> getAcceptedUsers(Long paperId) {
        String email = SecurityUtil.getCurrentUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);

        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(EntityNotFoundException::new);

        checkOwnerAndAccepted(user, paper);

        List<Invitation> acceptedInvitations = invitationRepository.findByPaperAndStatus(paper, InvitationStatus.ACCEPTED);

        // 해당 롤링 페이퍼에 대한 초대를 수락한 유저 반환
        List<User> receivers = acceptedInvitations.stream()
                .map(Invitation::getReceiver)
                .collect(Collectors.toList());

        return receivers;
    }

    /** 초대장 확인 */
    public Invitation findInvitation(Long invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(EntityNotFoundException::new);
    }

    /** 롤링 페이퍼를 작성한 유저 & 초대장을 수락한 유저인지 확인 */
    public void checkOwnerAndAccepted(User user, Paper paper) {
        boolean isOwner = paper.getUser().getId().equals(user.getId());

        boolean isAcceptedUser = invitationRepository.existsByPaperAndReceiverAndStatus(paper, user, InvitationStatus.ACCEPTED);

        if (!isOwner && !isAcceptedUser) {
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        }
    }

    /** invitation과 paper의 관계 끊기*/
    public void deleteInvitationsForPaper(Paper paper) {
        List<Invitation> invitations = invitationRepository.findByPaper(paper);

        for (Invitation invitation : invitations) {
            invitationRepository.delete(invitation);
        }
    }
}
