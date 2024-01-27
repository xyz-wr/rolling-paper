package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaperRepositoryTest {
    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByIsPublic 테스트")
    public void testFindByIsPublic() {
        // given
        User user = User.builder()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        Paper paper1 = new Paper(user,1L, "Paper 1", IsPublic.PUBLIC);
        Paper paper2 = new Paper(user, 2L, "Paper 2", IsPublic.FRIEND);
        Paper paper3 = new Paper(user, 3L, "Paper 3", IsPublic.PUBLIC);
        List<Paper> papers = Arrays.asList(paper1, paper2, paper3);
        paperRepository.saveAll(papers);

        // when
        List<Paper> result = paperRepository.findByIsPublic(IsPublic.PUBLIC);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Paper 1", result.get(0).getTitle());
        assertEquals("Paper 3", result.get(1).getTitle());
    }

    @Test
    @DisplayName("findAllByUserAndIsPublic 테스트")
    public void testFindAllByUserAndIsPublic() {
        // given
        User user = User.builder()
                .nickname("testNickname")
                .email("test@gmail.com")
                .build();
        userRepository.save(user);

        Paper paper1 = new Paper(user,1L, "Paper 1", IsPublic.PUBLIC);
        Paper paper2 = new Paper(user, 2L, "Paper 2", IsPublic.FRIEND);
        Paper paper3 = new Paper(user, 3L, "Paper 3", IsPublic.PUBLIC);
        List<Paper> papers = Arrays.asList(paper1, paper2, paper3);
        paperRepository.saveAll(papers);

        // when
        List<Paper> result = paperRepository.findAllByUserAndIsPublic(user, IsPublic.PUBLIC);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Paper 1", result.get(0).getTitle());
        assertEquals("Paper 3", result.get(1).getTitle());
    }
}