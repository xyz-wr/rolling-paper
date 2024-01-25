package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.dto.AuthDto;
import com.wrbread.roll.rollingpaper.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail 테스트")
    public void testFindByEmail() {
        // given
        User user = User.builder()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        // when
        Optional<User> findUser = userRepository.findByEmail("test@gmail.com");

        // then
        assertTrue(findUser.isPresent());
        assertEquals("test@gmail.com", findUser.get().getEmail());
    }

    @Test
    public void testExistsByNickname() {
        // given
        User user = User.builder()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        // when
        boolean existNickname = userRepository.existsByNickname("testNickname");

        // then
        assertTrue(existNickname);
    }

    @Test
    public void testExistsByEmail() {
        // given
        User user = User.builder()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        // when
        boolean existEmail = userRepository.existsByEmail("test@gmail.com");

        // then
        assertTrue(existEmail);
    }
}