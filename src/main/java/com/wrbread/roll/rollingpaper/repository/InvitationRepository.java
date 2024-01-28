package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByReceiver(User receiver);

    Invitation findByPaperAndReceiver(Paper paper, User receiver);

    boolean existsByPaperAndReceiverAndStatus(Paper paper, User receiver, InvitationStatus status);

    List<Invitation> findByPaperAndStatus(Paper paper, InvitationStatus status);

    List<Invitation> findAllByReceiverAndStatus(User receiver, InvitationStatus status);

    List<Invitation> findByPaper(Paper paper);
}
