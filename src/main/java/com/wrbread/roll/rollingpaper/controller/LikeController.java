package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    /** 좋아요 */
    @PostMapping("/papers/{paper-id}/messages/{message-id}/like")
    public String like(@PathVariable("paper-id") Long paperId,
                       @PathVariable("message-id") Long messageId) {
        likeService.like(messageId);

        return "redirect:/papers/{paper-id}/messages/{message-id}";
    }
}
