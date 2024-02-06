package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.entity.Like;
import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.LikeRepository;
import com.wrbread.roll.rollingpaper.repository.MessageRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class LikeServiceTest {
    @MockBean
    private MessageRepository messageRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LikeRepository likeRepository;

    @Autowired
    private LikeService likeService;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("like 테스트")
    public void like() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();

        User user1 = User.userDetail()
                .email(email)
                .build();

        User user2 = User.userDetail()
                .email("testEmail@gmail.com")
                .build();


        Long messageId = 1L;
        Message message = Message.builder()
                .id(messageId)
                .user(user2)
                .name("testMessage")
                .content("testContent")
                .likeCount(0)
                .build();


        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.of(user1));
        when(messageRepository.findById(messageId)).thenReturn(java.util.Optional.of(message));
        when(likeRepository.findByMessageAndUser(message, user1)).thenReturn(Optional.empty());

        //when
        likeService.like(messageId);

        // then
        assertEquals(1, message.getLikeCount());
        verify(likeRepository, times(1)).save(any(Like.class));
    }
}