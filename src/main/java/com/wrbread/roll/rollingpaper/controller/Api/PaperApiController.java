package com.wrbread.roll.rollingpaper.controller.Api;
import com.wrbread.roll.rollingpaper.util.UriCreator;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.service.PaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperApiController {
    private final PaperService paperService;

    /** 롤링 페이퍼 등록 */
    @PostMapping
    public ResponseEntity<PaperDto> postPaper(@RequestBody PaperDto paperDto) {
        Paper paper = paperService.savePaper(paperDto);

        URI location = UriCreator.createUri("/api/papers", paper.getId());

        return ResponseEntity.created(location).build();
    }

    /** 롤링 페이퍼 조회 */
    @GetMapping("/{paper-id}")
    public ResponseEntity<PaperDto> getPaper(@PathVariable("paper-id") Long paperId) {
        Paper paper = paperService.getPaper(paperId);

        return ResponseEntity.ok().body(new PaperDto(paper));
    }


    /** 롤링 페이퍼 수정 */
    @PatchMapping("/{paper-id}")
    public ResponseEntity<PaperDto> patchPaper(@PathVariable("paper-id") Long paperId,
                                               @RequestBody PaperDto paperDto) {
        Paper paper = paperService.updatePaper(paperId, paperDto);

        return ResponseEntity.ok().body(new PaperDto(paper));
    }

    /** IsPublic이 PUBLIC인 롤링 페이퍼 전체 조회 */
    @GetMapping("/public-paper-list")
    public ResponseEntity<List<PaperDto>> getPublicPapers() {
        List<PaperDto> responseDtos = paperService.getPublicPapers()
                .stream()
                .map(PaperDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    /** IsPublic이 FRIEND인 롤링 페이퍼 전체 조회 */
    @GetMapping("/friend-paper-list")
    public ResponseEntity<List<PaperDto>> getFriendPapers() {
        List<PaperDto> responseDtos = paperService.getFriendPapers()
                .stream()
                .map(PaperDto::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    /** 롤링 페이퍼 삭제 */
    @DeleteMapping("/{paper-id}")
    public ResponseEntity<Void> deletePaper(@PathVariable("paper-id") Long paperId) {
        paperService.deletePaper(paperId);

        return ResponseEntity.noContent().build();
    }
}