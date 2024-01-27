package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import com.wrbread.roll.rollingpaper.repository.PaperRepository;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PaperServiceTest {
    @MockBean
    private PaperRepository paperRepository;

    @Autowired
    private PaperService paperService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 저장")
    void testSavePaper() {
        // given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .email(email)
                .build();

        PaperDto paperDto = new PaperDto();
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity(user);

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.save(any(Paper.class))).thenReturn(paper);

        // when
        Paper savedPaper = paperService.savePaper(paperDto);

        // then
        Assertions.assertEquals(savedPaper.getTitle(), "Test Paper");
        Assertions.assertEquals(savedPaper.getIsPublic(), IsPublic.PUBLIC);

        verify(paperRepository, times(1)).save(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 조회")
    void testGetPaper() {
        //given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = paperDto.toEntity(user);

        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));

        // when
        Paper getPaper = paperService.getPaper(paperId);

        // then
        Assertions.assertEquals(getPaper.getId(), paperId);
        Assertions.assertEquals(getPaper.getTitle(), "Test Paper");
        Assertions.assertEquals(getPaper.getIsPublic(), IsPublic.PUBLIC);

        // verify
        verify(userRepository, times(1)).findByEmail(email);
        verify(paperRepository, times(1)).findById(paperId);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 수정")
    void testUpdatePaper() {
        //given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Long paperId = 1L;
        PaperDto updatedPaperDto = new PaperDto();
        updatedPaperDto.setId(paperId);
        updatedPaperDto.setTitle("Updated Paper");
        updatedPaperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper = Paper.builder()
                .id(1L)
                .user(user)
                .title(updatedPaperDto.getTitle())
                .isPublic(updatedPaperDto.getIsPublic())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));
        when(paperRepository.save(paper)).thenReturn(paper);

        // when
        Paper updatedPaper = paperService.updatePaper(paperId, updatedPaperDto);

        // then
        Assertions.assertEquals(updatedPaper.getId(), paperId);
        Assertions.assertEquals(updatedPaper.getTitle(), "Updated Paper");
        Assertions.assertEquals(updatedPaper.getIsPublic(), IsPublic.PUBLIC);

        // verify
        verify(userRepository, times(1)).findByEmail(email);
        verify(paperRepository, times(1)).findById(1L);
        verify(paperRepository, times(1)).save(paper);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("IsPublic이 PUBLIC인 롤링 페이퍼 전체 조회")
    void testGetPublicPapers() {
        //given
        PaperDto paperDto = new PaperDto();
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.PUBLIC);

        Paper paper1 = Paper.builder()
                .id(1L)
                .title(paperDto.getTitle())
                .isPublic(paperDto.getIsPublic())
                .build();

        Paper paper2 = Paper.builder()
                .id(2L)
                .title(paperDto.getTitle())
                .isPublic(paperDto.getIsPublic())
                .build();

        when(paperRepository.findByIsPublic(IsPublic.PUBLIC)).thenReturn(Arrays.asList(paper1, paper2));

        //when
        List<Paper> publicPapers = paperService.getPublicPapers();

        // then
        Assertions.assertEquals(publicPapers.size(), 2);
        Assertions.assertTrue(publicPapers.contains(paper1));
        Assertions.assertTrue(publicPapers.contains(paper2));

        // verify
        verify(paperRepository, times(1)).findByIsPublic(IsPublic.PUBLIC);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("IsPublic이 Friend인 롤링 페이퍼 전체 조회")
    void testGetFriendPapers() {
        //given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        PaperDto paperDto = new PaperDto();
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.FRIEND);

        Paper paper1 = Paper.builder()
                .id(1L)
                .title(paperDto.getTitle())
                .isPublic(paperDto.getIsPublic())
                .build();

        Paper paper2 = Paper.builder()
                .id(2L)
                .title(paperDto.getTitle())
                .isPublic(paperDto.getIsPublic())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findAllByUserAndIsPublic(user, IsPublic.FRIEND)).thenReturn(Arrays.asList(paper1, paper2));

        //when
        List<Paper> friendPapers = paperService.getFriendPapers();

        // then
        Assertions.assertEquals(friendPapers.size(), 2);
        Assertions.assertTrue(friendPapers.contains(paper1));
        Assertions.assertTrue(friendPapers.contains(paper2));

        // verify
        verify(userRepository, times(1)).findByEmail(email);
        verify(paperRepository, times(1)).findAllByUserAndIsPublic(user, IsPublic.FRIEND);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("롤링 페이퍼 삭제")
    void testDeletePaper() {
        //given
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        String email = authentication.getName();
        User user = User.builder()
                .id(1L)
                .email(email)
                .build();

        Long paperId = 1L;
        PaperDto paperDto = new PaperDto();
        paperDto.setId(paperId);
        paperDto.setTitle("Test Paper");
        paperDto.setIsPublic(IsPublic.FRIEND);

        Paper paper = Paper.builder()
                .id(paperId)
                .user(user)
                .title(paperDto.getTitle())
                .isPublic(paperDto.getIsPublic())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByEmail(email)).thenReturn(java.util.Optional.ofNullable(user));
        when(paperRepository.findById(paperId)).thenReturn(Optional.of(paper));

        //when
        paperService.deletePaper(paperId);

        // verify
        verify(userRepository, times(1)).findByEmail(email);
        verify(paperRepository, times(1)).findById(paperId);
        verify(paperRepository, times(1)).delete(paper);
    }
}