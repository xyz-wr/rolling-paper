package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
    Optional<ProfileImg> findByImgUrl(String imgUrl);
}
