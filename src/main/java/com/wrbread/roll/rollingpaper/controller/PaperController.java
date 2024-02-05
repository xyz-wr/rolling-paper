package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.service.MessageService;
import com.wrbread.roll.rollingpaper.service.PaperService;
import com.wrbread.roll.rollingpaper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/papers")
public class PaperController {
    private final PaperService paperService;

    private final MessageService messageService;

    private final UserService userService;


    /** public 롤링 페이퍼 전체 조회 */
    @GetMapping("/all-public-papers")
    public String publicList(Model model, Authentication auth) {
        // 사용자가 인증되어 있지 않은 경우
        if (auth == null) {
            return "redirect:/";
        }

        List<PaperDto> paperDtos = paperService.getPublicPapers()
                .stream()
                .map(PaperDto::new)
                .collect(Collectors.toList());

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtos", paperDtos);

        return "paper/all-public-papers";
    }

    /** friend 롤링 페이퍼 전체 조회 */
    @GetMapping("/all-friend-papers")
    public String friendList(Model model, Authentication auth) {
        List<PaperDto> paperDtos = paperService.getFriendPapers()
                .stream()
                .map(PaperDto::new)
                .collect(Collectors.toList());

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtos", paperDtos);

        return "paper/all-friend-papers";
    }

    /** 롤링 페이퍼 등록 화면 로딩 */
    @GetMapping("/write")
    public String save(Model model) {
        model.addAttribute("paperDto", new PaperDto());

        return "paper/write";
    }

    /** 롤링 페이퍼 등록 */
    @PostMapping("/write")
    public String write(PaperDto paperDto, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "paper/write";
        }

        if (paperDto.getTitle().isEmpty() || paperDto.getTitle()== null) {
            model.addAttribute("errorMessage", "제목을 입력해주세요.");
            return "paper/write";
        }

        if (paperDto.getIsPublic() == null) {
            model.addAttribute("errorMessage", "공개 여부를 선택해주세요.");
            return "paper/write";
        }

        if (paperDto.getTitle().length() > 10 || paperDto.getTitle().length() < 1) {
            model.addAttribute("errorMessage", "1글자 이상 10글자 이하로 작성해주세요.");
            return "paper/write";
        }

        try {
            paperService.savePaper(paperDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "롤링페이퍼 등록 중 에러가 발생하였습니다.");
            return "paper/write";
        }

        if (paperDto.getIsPublic().equals(IsPublic.FRIEND)) {
            return "redirect:/papers/all-friend-papers";
        }

        return "redirect:/papers/all-public-papers";
    }

    /** 롤링 페이퍼 수정 화면 로딩 */
    @GetMapping("/edit/{paper-id}")
    public String update(@PathVariable("paper-id") Long paperId, Model model) {
        Paper paper= paperService.getEditPaper(paperId);
        PaperDto paperDto = new PaperDto(paper);

        model.addAttribute("paperDto", paperDto);

        return "paper/write";
    }

    /** 롤링 페이퍼 수정 */
    @PostMapping("/edit/{paper-id}")
    public String edit(@PathVariable("paper-id") Long id, PaperDto paperDto,
                       BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "paper/write";
        }

        if (paperDto.getTitle().isEmpty()) {
            model.addAttribute("errorMessage", "제목을 입력해주세요.");
            return "paper/write";
        }

        if (paperDto.getTitle().length() > 10 || paperDto.getTitle().length() < 1) {
            model.addAttribute("errorMessage", "1글자 이상 10글자 이하로 작성해주세요.");
            return "paper/write";
        }

        try {
            paperService.updatePaper(id, paperDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "롤링페이퍼 등록 중 에러가 발생하였습니다.");
            return "paper/write";
        }

        if (paperDto.getIsPublic().equals(IsPublic.FRIEND)) {
            return "redirect:/papers/all-friend-papers";
        }

        return "redirect:/papers/all-public-papers";
    }

    /** 롤링 페이퍼 조회
     * 메시지 목록 포함
     * */
    @GetMapping("/{paper-id}")
    public String detail(@PathVariable("paper-id") Long paperId, Model model, Authentication auth) {
        Paper paper = paperService.getPaper(paperId);
        PaperDto paperDto = new PaperDto(paper);

        List<MessageDto> messageDtos = messageService.getMessages(paperId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperId", paperId);
        model.addAttribute("paperDto", paperDto);
        model.addAttribute("messageDtos", messageDtos);

        return "paper/detail";
    }

    /** 롤링 페이퍼 삭제 */
    @GetMapping("/delete/{paper-id}")
    public String delete(@PathVariable("paper-id") Long id) {
        paperService.deletePaper(id);

        return "redirect:/papers/all-public-papers";
    }

    /** 롤링 페이퍼에 초대할 유저 검색 */
    @GetMapping("/{paper-id}/search/codename")
    public String search(@PathVariable("paper-id") Long paperId,
                         String codename, Model model, Authentication auth) {
        String sender = auth.getName();
        User user = userService.findCodename(codename);

        model.addAttribute("sender", sender);
        model.addAttribute("user", user);
        model.addAttribute("paperId", paperId);
        return "paper/search";
    }
}
