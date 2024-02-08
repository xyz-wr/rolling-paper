package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
