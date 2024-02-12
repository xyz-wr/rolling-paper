package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.InvitationRepository;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaperService {
    private final PaperRepository paperRepository;

    private final UserService userService;

    private final InvitationService invitationService;

    private final UserRepository userRepository;

    private final InvitationRepository invitationRepository;

    /** 롤링 페이퍼 등록 */
    public Paper savePaper(PaperDto paperDto) {
        User user = userService.verifiedEmail();

        if (user.isSubscriber() || user.getWriteCount() > 0) {
            // 이용권을 구매한 경우 또는 작성 횟수 제한이 남아있는 경우
            if (!user.isSubscriber()) {
                // 이용권을 구매하지 않은 경우에만 작성 횟수 감소
                user.decreaseWriteCount();
                userRepository.save(user);
            }

            Paper paper = paperDto.toEntity(user);
            return paperRepository.save(paper);
        } else {
            throw new BusinessLogicException(ExceptionCode.WRITE_COUNT_EXCEEDED);
        }
    }

    public Paper getEditPaper(Long id) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        checkOwner(user, paper);

        return paper;
    }


    /** 롤링 페이퍼 조회
     * 전체 공개인 경우 모든 유저 조회 가능
     * 친구 공개인 경우 롤링 페이퍼 작성자와 허락된 유저만 조회 가능*/
    public Paper getPaper(Long id) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        //친구 공개인 경우 허락된 유저인지 확인
        if (paper.getIsPublic().equals(IsPublic.FRIEND)) {
            invitationService.checkOwnerAndAccepted(user, paper);
        }

        return paper;
    }

    /** 롤링 페이퍼 수정
     * 롤링 페이퍼 작성자만 수정 가능
     * */
    public Paper updatePaper(Long id, PaperDto paperDto) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        checkOwner(user, paper);

        //isPublic 값 기존 값으로 유지하기
        if (paperDto.getIsPublic() == null) {
            paperDto.setIsPublic(paper.getIsPublic());
        }

        paper.updatePaper(paperDto);

        return paperRepository.save(paper);
    }

    /** IsPublic이 PUBLIC인 롤링 페이퍼 전체 조회 */
    public List<Paper> getPublicPapers() {

        return paperRepository.findByIsPublic(IsPublic.PUBLIC);
    }


    /** IsPublic이 FRIEND인 롤링 페이퍼 전체 조회 */
    public List<Paper> getFriendPapers() {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findAllByUserAndIsPublic(user, IsPublic.FRIEND);

        //초대 수락한 책 추가
        List<Invitation> invitations = invitationRepository.findAllByReceiverAndStatus(user, InvitationStatus.ACCEPTED);
        for (Invitation invitation : invitations) {
            papers.add(invitation.getPaper());
        }

        return papers;
    }

    /** 내가 작성한 public 롤링 페이퍼 전체 조회 */
    public List<Paper> getMyPublicPapers() {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findAllByUserAndIsPublic(user, IsPublic.PUBLIC);

        return papers;
    }


    /** 내가 작성한 friend 롤링 페이퍼 전체 조회 */
    public List<Paper> getMyFriendPapers() {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findAllByUserAndIsPublic(user, IsPublic.FRIEND);

        return papers;
    }


    /** 롤링 페이퍼 삭제
     * 롤링 페이퍼 작성자만 삭제 가능
     * */
    public void deletePaper(Long id) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PAPER_NOT_FOUND));

        checkOwner(user, paper);

        invitationService.deleteInvitationsForPaper(paper);
        paperRepository.delete(paper);
    }

    /** 롤링 페이퍼를 작성한 유저인지 확인 */
    public void checkOwner(User user, Paper paper) {
        boolean isOwner = paper.getUser().getId().equals(user.getId());

        if (!isOwner) {
            throw new BusinessLogicException(ExceptionCode.NO_PERMISSION_ACCESS);
        }
    }

    /** public 롤링 페이퍼 검색 */
    public List<Paper> searchPublicPapers (String keyword) {
        List<Paper> papers = paperRepository.findByTitleContainingAndIsPublic(keyword, IsPublic.PUBLIC);

        return papers;
    }

    /** friend 롤링 페이퍼 검색 */
    public List<Paper> searchFriendPapers (String keyword) {
        User user = userService.verifiedEmail();

        List<Paper> papers = paperRepository.findAllByUserAndTitleContainingAndIsPublic(user, keyword, IsPublic.FRIEND);

        // 초대 수락한 책 추가
        List<Invitation> invitations = invitationRepository.findAllByReceiverAndStatus(user, InvitationStatus.ACCEPTED);
        for (Invitation invitation : invitations) {
            if (invitation.getPaper().getTitle().contains(keyword)) {
                papers.add(invitation.getPaper());
            }
        }

        return papers;
    }
}