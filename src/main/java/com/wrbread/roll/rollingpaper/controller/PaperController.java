package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.service.MessageService;
import com.wrbread.roll.rollingpaper.service.PaperService;
import com.wrbread.roll.rollingpaper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public String publicList(@PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model, Authentication auth) {
        // 사용자가 인증되어 있지 않은 경우
        if (auth == null) {
            return "redirect:/";
        }

        Page<Paper> paper = paperService.getPublicPapers(pageable);
        List<PaperDto> paperDtos = paper.map(PaperDto::new).getContent();
        Page<PaperDto> paperDtoPage = new PageImpl<>(paperDtos, pageable, paper.getTotalElements());


        int totalPages = paperDtoPage.getTotalPages();
        int pageNumber = pageable.getPageNumber();
        int navPageSize = 3;
        int startPage = Math.max(0, ((pageNumber / navPageSize) * navPageSize) + 1);
        int tempEndPage = startPage + navPageSize - 1;
        int endPage = (tempEndPage < totalPages) ? tempEndPage : totalPages;

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtoPage", paperDtoPage);
        model.addAttribute("paperCnt", paper.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("tempEndPage", tempEndPage);
        model.addAttribute("endPage", endPage);

        return "paper/all-public-papers";
    }

    /** friend 롤링 페이퍼 전체 조회 */
    @GetMapping("/all-friend-papers")
    public String friendList(@PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model, Authentication auth) {
        Page<Paper> paper = paperService.getFriendPapers(pageable);
        List<PaperDto> paperDtos = paper.map(PaperDto::new).getContent();
        Page<PaperDto> paperDtoPage = new PageImpl<>(paperDtos, pageable, paper.getTotalElements());

        int totalPages = paperDtoPage.getTotalPages();
        int pageNumber = pageable.getPageNumber();
        int navPageSize = 3;
        int startPage = Math.max(0, ((pageNumber / navPageSize) * navPageSize) + 1);
        int tempEndPage = startPage + navPageSize - 1;
        int endPage = (tempEndPage < totalPages) ? tempEndPage : totalPages;

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtoPage", paperDtoPage);
        model.addAttribute("paperCnt", paper.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("tempEndPage", tempEndPage);
        model.addAttribute("endPage", endPage);

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
    public String detail(@PathVariable("paper-id") Long paperId,
                         @PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                         Model model,  Authentication auth) {
        Paper paper = paperService.getPaper(paperId);
        PaperDto paperDto = new PaperDto(paper);



        Page<Message> message = messageService.getMessages(paperId, pageable);
        List<MessageDto> messageDtos = message.map(MessageDto::new).getContent();
        Page<MessageDto> messageDtoPage = new PageImpl<>(messageDtos, pageable, message.getTotalElements());

        int totalPages = messageDtoPage.getTotalPages();
        int pageNumber = pageable.getPageNumber();
        int navPageSize = 3;
        int startPage = Math.max(0, ((pageNumber / navPageSize) * navPageSize) + 1);
        int tempEndPage = startPage + navPageSize - 1;
        int endPage = (tempEndPage < totalPages) ? tempEndPage : totalPages;

        model.addAttribute("email", auth.getName());
        model.addAttribute("messageDtoPage", messageDtoPage);
        model.addAttribute("paperCnt", message.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("tempEndPage", tempEndPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperId", paperId);
        model.addAttribute("paperDto", paperDto);
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
    public String searchPublicPaper(@PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                    String keyword, Model model, Authentication auth) {

        Page<Paper> paper = paperService.searchPublicPapers(keyword,pageable);
        List<PaperDto> paperDtos = paper.map(PaperDto::new).getContent();
        Page<PaperDto> paperDtoPage = new PageImpl<>(paperDtos, pageable, paper.getTotalElements());


        int totalPages = paperDtoPage.getTotalPages();
        int pageNumber = pageable.getPageNumber();
        int navPageSize = 3;
        int startPage = Math.max(0, ((pageNumber / navPageSize) * navPageSize) + 1);
        int tempEndPage = startPage + navPageSize - 1;
        int endPage = (tempEndPage < totalPages) ? tempEndPage : totalPages;

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtoPage", paperDtoPage);
        model.addAttribute("paperCnt", paper.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("tempEndPage", tempEndPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        return "paper/search-public-papers";
    }

    /** FRIEND 롤링 페이퍼 검색 */
    @GetMapping("/search-friend-paper")
    public String searchFriendPaper(@PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable,
                                    String keyword, Model model, Authentication auth) {
        Page<Paper> paper = paperService.searchFriendPapers(keyword,pageable);
        List<PaperDto> paperDtos = paper.map(PaperDto::new).getContent();
        Page<PaperDto> paperDtoPage = new PageImpl<>(paperDtos, pageable, paper.getTotalElements());


        int totalPages = paperDtoPage.getTotalPages();
        int pageNumber = pageable.getPageNumber();
        int navPageSize = 3;
        int startPage = Math.max(0, ((pageNumber / navPageSize) * navPageSize) + 1);
        int tempEndPage = startPage + navPageSize - 1;
        int endPage = (tempEndPage < totalPages) ? tempEndPage : totalPages;

        model.addAttribute("email", auth.getName());
        model.addAttribute("paperDtoPage", paperDtoPage);
        model.addAttribute("paperCnt", paper.getTotalElements());
        model.addAttribute("startPage", startPage);
        model.addAttribute("tempEndPage", tempEndPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        return "paper/search-friend-papers";
    }
}
