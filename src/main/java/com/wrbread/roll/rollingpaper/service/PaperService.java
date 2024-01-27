package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
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

    /** 롤링 페이퍼 등록 */
    public Paper savePaper(PaperDto paperDto) {
        User user = userService.verifiedEmail();
        Paper paper = paperDto.toEntity(user);
        return paperRepository.save(paper);
    }

    /** 롤링 페이퍼 조회
     * 전체 공개인 경우 모든 유저 조회 가능
     * 친구 공개인 경우 롤링 페이퍼 작성자와 허락된 유저만 조회 가능*/
    public Paper getPaper(Long id) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        boolean isOwner = paper.getUser().getId().equals(user.getId());

        //친구 공개인 경우 허락된 유저인지 확인 && 추후에 초대장 수락 유저 추가
        if (paper.getIsPublic().equals(IsPublic.FRIEND)) {
            if (!isOwner) {
                throw new IllegalArgumentException("해당 권한이 없습니다.");
            }
        }

        return paper;
    }

    /** 롤링 페이퍼 수정
     * 롤링 페이퍼 작성자만 수정 가능
     * */
    public Paper updatePaper(Long id, PaperDto paperDto) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        boolean isOwner = paper.getUser().getId().equals(user.getId());

        // 작성자인지 확인
        if (!isOwner) {
            throw new IllegalArgumentException("해당 권한이 없습니다.");
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

        return paperRepository.findAllByUserAndIsPublic(user, IsPublic.FRIEND);
    }


    /** 롤링 페이퍼 삭제 */
    public void deletePaper(Long id) {
        User user = userService.verifiedEmail();

        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        boolean isOwner = paper.getUser().getId().equals(user.getId());

        // 작성자인지 확인
        if (!isOwner) {
            throw new IllegalArgumentException("해당 권한이 없습니다.");
        }

        paperRepository.delete(paper);
    }

}