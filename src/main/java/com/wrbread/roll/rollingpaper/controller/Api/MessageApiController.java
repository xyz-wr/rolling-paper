package com.wrbread.roll.rollingpaper.controller.Api;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.service.MessageService;
import com.wrbread.roll.rollingpaper.util.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/papers/{paper-id}/messages")
@RequiredArgsConstructor
public class MessageApiController {
    private final MessageService messageService;

    /** 메시지 등록 */
    @PostMapping
    public ResponseEntity<MessageDto> postMessage(@PathVariable("paper-id") Long paperId,
                                                  @Valid @RequestBody MessageDto messageDto) {
        Message message = messageService.saveMessages(paperId, messageDto);

        URI location = UriCreator.createUri("/api/papers/" + paperId + "/messages", message.getId());

        return ResponseEntity.created(location).build();
    }

    /** 메시지 조회 */
    @GetMapping("/{message-id}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable("paper-id") Long paperId,
                                                 @PathVariable("message-id") Long messageId) {
        Message message = messageService.getMessage(paperId, messageId);

        return ResponseEntity.ok().body(new MessageDto(message));
    }

    /** 메시지 수정 */
    @PatchMapping("/{message-id}")
    public ResponseEntity<MessageDto> patchMessage(@PathVariable("paper-id") Long paperId,
                                                   @PathVariable("message-id") Long messageId,
                                                   @Valid @RequestBody MessageDto messageDto) {
        Message message = messageService.updateMessage(paperId, messageId, messageDto);

        return ResponseEntity.ok().body(new MessageDto(message));
    }

    /** 특정 롤링페이퍼의 전체 메시지 조회 */
    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable("paper-id") Long paperId,
                                                        @PageableDefault(page = 0, size = 6, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        List<MessageDto> responseDtos = messageService.getMessages(paperId,pageable)
                .stream()
                .map(MessageDto::new)
                .toList();

        return ResponseEntity.ok().body(responseDtos);
    }

    /** 내가 작성한 public 메시지 전체 조회 */
    @GetMapping("/my-public-message-list")
    public ResponseEntity<List<MessageDto>> myPublicMessageList(@PathVariable("paper-id") Long paperId) {
        List<MessageDto> responseDtos = messageService.getMyPublicMessages()
                .stream()
                .map(MessageDto::new)
                .toList();

        return ResponseEntity.ok().body(responseDtos);
    }

    /** 내가 작성한 friend 메시지 전체 조회 */
    @GetMapping("/my-friend-message-list")
    public ResponseEntity<List<MessageDto>> myFriendList(@PathVariable("paper-id") Long paperId) {
        List<MessageDto> responseDtos = messageService.getMyFriendMessages()
                .stream()
                .map(MessageDto::new)
                .toList();

        return ResponseEntity.ok().body(responseDtos);
    }


    /** 메시지 삭제 */
    @DeleteMapping("/{message-id}")
    public ResponseEntity<MessageDto> deleteMessage(@PathVariable("paper-id") Long paperId,
                                                    @PathVariable("message-id") Long messageId) {

        messageService.deleteMessage(paperId, messageId);
        return ResponseEntity.noContent().build();
    }

    /** 내가 좋아요 누른 메시지 전체 조회 */
    @GetMapping("/like/my-like-message-list")
    public ResponseEntity<List<MessageDto>> getMyLikeMessages(@PathVariable("paper-id") Long paperId) {
        List<MessageDto> messages = messageService.getMyLikes()
                .stream()
                .map(MessageDto::new)
                .toList();

        return ResponseEntity.ok(messages);
    }

}
