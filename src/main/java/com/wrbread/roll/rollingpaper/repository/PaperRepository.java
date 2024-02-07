package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    List<Paper> findByIsPublic(IsPublic isPublic);

    List<Paper> findAllByUserAndIsPublic(User user, IsPublic isPublic); //특정 사용자가 작성한 공개 여부의 모든 논문을 반환

    List<Paper> findByTitleContaining(String keyword);
}