package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByPaperIdAndId(Long paperId, Long messageId);

    List<Message> findByPaper(Paper paper);

    List<Message> findByPaperAndUser(Paper paper, User user);
}
