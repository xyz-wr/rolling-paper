package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import com.wrbread.roll.rollingpaper.model.entity.Like;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.LikeRepository;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;

    private final MessageRepository messageRepository;

    private final UserService userService;

    /** 좋아요
     * 메시지를 작성한 유저는 해당 메시지에 좋아요를 누를 수 없음
     * 이미 좋아요가 눌러진 경우 다시 누르면 좋아요가 취소됨 */
    public void like(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));

        User user = userService.verifiedEmail();

        // 메시지를 작성한 유저와 좋아요를 누르는 유저가 같은지 확인
        if (message.getUser().equals(user)) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_LIKE_OWN_MESSAGE);
        }

        // 사용자가 이미 해당 메시지에 좋아요를 눌렀는지 확인
        Optional<Like> existingLike = likeRepository.findByMessageAndUser(message, user);
        if (existingLike.isPresent()) {
            // 이미 좋아요를 누른 경우 좋아요를 취소
            message.decreaseLike();
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요 추가
            Like like = Like.builder()
                    .user(user)
                    .message(message)
                    .build();

            message.increaseLike();
            likeRepository.save(like);
        }
    }
}
