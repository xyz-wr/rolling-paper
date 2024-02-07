package com.wrbread.roll.rollingpaper.controller.Api;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Like;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class LikeApiController {
    private final LikeService likeService;

    /** 좋아요 */
    @PostMapping("/api/messages/{message-id}/likes")
    public ResponseEntity<Void> like(@PathVariable("message-id") Long messageId) {
        likeService.like(messageId);

        return ResponseEntity.ok().build();
    }
}
