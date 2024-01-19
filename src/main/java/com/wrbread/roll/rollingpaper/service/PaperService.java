package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
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

    /** 롤링 페이퍼 등록 */
    public Paper savePaper(PaperDto paperDto) {
        Paper paper = paperDto.toEntity();
        return paperRepository.save(paper);
    }

    /** 롤링 페이퍼 조회 */
    public Paper getPaper(Long id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return paper;
    }

    /** 롤링 페이퍼 수정 */
    public Paper updatePaper(Long id, PaperDto paperDto) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        paper.updatePaper(paperDto);

        return paperRepository.save(paper);
    }

    /** IsPublic이 PUBLIC인 롤링 페이퍼 전체 조회 */
    public List<Paper> getPublicPapers() {
        return paperRepository.findByIsPublic(IsPublic.PUBLIC);
    }


    /** IsPublic이 FRIEND인 롤링 페이퍼 전체 조회 */
    public List<Paper> getFriendPapers() {
        return paperRepository.findByIsPublic(IsPublic.FRIEND);
    }


    /** 롤링 페이퍼 삭제 */
    public void deletePaper(Long id) {
        Paper paper = paperRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        paperRepository.delete(paper);
    }

}