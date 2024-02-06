package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import com.wrbread.roll.rollingpaper.model.entity.Like;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LikeRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Test
    @DisplayName("findByMessageAndUser 테스트")
    public void testFindByMessageAndUser() {
        // given
        User user = User.userDetail()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        Message message = Message.builder()
                .user(user)
                .id(1L)
                .name("testName")
                .content("testContent")
                .build();
        messageRepository.save(message);

        Like like = Like.builder()
                .message(message)
                .user(user)
                .build();
        likeRepository.save(like);

        // when
        Optional<Like> result = likeRepository.findByMessageAndUser(message, user);

        // then
        assertNotNull(result);
        assertEquals(user, result.get().getUser());
        assertEquals(message, result.get().getMessage());
    }
}