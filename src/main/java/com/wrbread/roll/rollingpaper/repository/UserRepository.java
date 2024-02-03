package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findByCodename(String codename);
}
