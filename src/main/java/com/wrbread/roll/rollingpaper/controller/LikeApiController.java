package com.wrbread.roll.rollingpaper.controller;

import com.wrbread.roll.rollingpaper.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages/{message-id}/likes")
@RequiredArgsConstructor
public class LikeApiController {
    private final LikeService likeService;

    /** 좋아요 */
    @PostMapping
    public ResponseEntity<Void> like(@PathVariable("message-id") Long messageId) {
        likeService.like(messageId);

        return ResponseEntity.ok().build();
    }
}
