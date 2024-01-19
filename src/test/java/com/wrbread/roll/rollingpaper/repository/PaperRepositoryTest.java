package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PaperRepositoryTest {
    @Autowired
    private PaperRepository paperRepository;

    @Test
    @DisplayName("findByIsPublic 테스트")
    public void testFindByIsPublic() {
        // given
        Paper paper1 = new Paper(1L, "Paper 1", IsPublic.PUBLIC);
        Paper paper2 = new Paper(2L, "Paper 2", IsPublic.FRIEND);
        Paper paper3 = new Paper(3L, "Paper 3", IsPublic.PUBLIC);
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
}