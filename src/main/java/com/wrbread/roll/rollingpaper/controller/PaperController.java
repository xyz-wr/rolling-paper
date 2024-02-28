package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
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
                .toList();

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
                .toList();

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
    public String write(PaperDto paperDto, BindingResult bindingResult,
                        Model model) {

        if (bindingResult.hasErrors()) {
            return "paper/write";
        }

        try {
            paperService.savePaper(paperDto);
        } catch (BusinessLogicException ex) {
            if (ex.getExceptionCode() == ExceptionCode.WRITE_COUNT_EXCEEDED) {
                model.addAttribute("errorMessage", ex.getMessage());
                return "user/purchase";
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
                return "paper/write";
            }
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

        try {
            paperService.updatePaper(id, paperDto);
        } catch (BusinessLogicException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "paper/write";
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
    public String detail(@PathVariable("paper-id") Long paperId, Model model,
                         Authentication auth) {
        Paper paper = paperService.getPaper(paperId);
        PaperDto paperDto = new PaperDto(paper);

        List<MessageDto> messageDtos = messageService.getMessages(paperId)
                .stream()
                .map(MessageDto::new)
                .toList();

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

    /** 내가 작성한 public 롤링 페이퍼 전체 조회 */
    @GetMapping("/my-public-paper-list")
    public String myPublicList(Model model) {
        List<PaperDto> paperDtos = paperService.getMyPublicPapers()
                .stream()
                .map(PaperDto::new)
                .toList();

        model.addAttribute("paperDtos", paperDtos);

        return "paper/my-public-papers";
    }

    /** 내가 작성한 friend 롤링 페이퍼 전체 조회 */
    @GetMapping("/my-friend-paper-list")
    public String myFriendList(Model model) {
        List<PaperDto> paperDtos = paperService.getMyFriendPapers()
                .stream()
                .map(PaperDto::new)
                .toList();

        model.addAttribute("paperDtos", paperDtos);

        return "paper/my-friend-papers";
    }

    /** PUBLIC 롤링 페이퍼 검색 */
    @GetMapping("/search-public-paper")
    public String searchPublicPaper(String keyword, Model model, Authentication auth) {
        List<PaperDto> paperDtos = paperService.searchPublicPapers(keyword)
                .stream()
                .map(PaperDto::new)
                .toList();

        model.addAttribute("paperDtos", paperDtos);
        model.addAttribute("email", auth.getName());

        return "paper/search-public-papers";
    }

    /** FRIEND 롤링 페이퍼 검색 */
    @GetMapping("/search-friend-paper")
    public String searchFriendPaper(String keyword, Model model, Authentication auth) {
        List<PaperDto> paperDtos = paperService.searchFriendPapers(keyword)
                .stream()
                .map(PaperDto::new)
                .toList();

        model.addAttribute("paperDtos", paperDtos);
        model.addAttribute("email", auth.getName());

        return "paper/search-friend-papers";
    }
}
