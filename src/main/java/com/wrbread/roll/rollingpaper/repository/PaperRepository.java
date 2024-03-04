package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    Page<Paper> findByIsPublic(IsPublic isPublic, Pageable pageable);

    List<Paper> findByIsPublic(IsPublic isPublic);

    List<Paper> findAllByUserAndIsPublic(User user, IsPublic isPublic, Sort sort);

    Page<Paper> findByTitleContainingAndIsPublic(String keyword, IsPublic isPublic, Pageable pageable);

    Page<Paper> findAllByUserAndTitleContainingAndIsPublic(User user, String keyword, IsPublic isPublic, Pageable pageable);
}
