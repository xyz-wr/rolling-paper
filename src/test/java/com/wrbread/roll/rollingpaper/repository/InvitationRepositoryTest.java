package com.wrbread.roll.rollingpaper.repository;

import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class InvitationRepositoryTest {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Test
    @DisplayName("findByPaperIdAndId 테스트")
    public void testFindByReceiver() {
        // given
        User receiver = User.userDetail()
                .nickname("testNickname")
                .email("receiver@gmail.com")
                .build();
        userRepository.save(receiver);

        Invitation invitation1 = Invitation.builder()
                .status(InvitationStatus.PENDING)
                .receiver(receiver)
                .build();

        Invitation invitation2 = Invitation.builder()
                .status(InvitationStatus.PENDING)
                .receiver(receiver)
                .build();

        List<Invitation> invitations = Arrays.asList(invitation1, invitation2);
        invitationRepository.saveAll(invitations);

        // when
        List<Invitation> result = invitationRepository.findByReceiver(receiver);

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(invitation1));
        assertTrue(result.contains(invitation2));
    }

    @Test
    @DisplayName("findByPaperAndReceiver 테스트")
    public void testFindByPaperAndReceiver() {
        User receiver = User.userDetail()
                .nickname("testNickname")
                .email("receiver@gmail.com")
                .build();
        userRepository.save(receiver);

        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);

        Invitation invitation = Invitation.builder()
                .paper(paper)
                .receiver(receiver)
                .status(InvitationStatus.PENDING)
                .receiver(receiver)
                .build();
        invitationRepository.save(invitation);

        // when
        Invitation result = invitationRepository.findByPaperAndReceiver(paper, receiver);

        //then
        assertNotNull(result);
        assertEquals(paper, result.getPaper());
        assertEquals(receiver, result.getReceiver());
    }

    @Test
    @DisplayName("findByPaperAndReceiverAndStatus 테스트")
    public void testFindByPaperAndReceiverAndStatus() {
        User receiver = User.userDetail()
                .nickname("testNickname")
                .email("receiver@gmail.com")
                .build();
        userRepository.save(receiver);

        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);

        Invitation invitation = Invitation.builder()
                .paper(paper)
                .receiver(receiver)
                .status(InvitationStatus.ACCEPTED)
                .receiver(receiver)
                .build();
        invitationRepository.save(invitation);

        // when
        Optional<Invitation> result = invitationRepository.findByPaperAndReceiverAndStatus(paper, receiver, InvitationStatus.ACCEPTED);

        //then
        assertTrue(result.isPresent());
        assertEquals(paper, result.get().getPaper());
        assertEquals(receiver, result.get().getReceiver());
        assertEquals(InvitationStatus.ACCEPTED, result.get().getStatus());
    }

    @Test
    @DisplayName("existsByPaperAndReceiverAndStatus 테스트")
    public void testExistsByPaperAndReceiverAndStatus() {
        // given
        User receiver = User.userDetail()
                .nickname("testNickname")
                .email("receiver@gmail.com")
                .build();
        userRepository.save(receiver);

        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);

        Invitation invitation = Invitation.builder()
                .paper(paper)
                .receiver(receiver)
                    .status(InvitationStatus.ACCEPTED)
                .receiver(receiver)
                .build();
        invitationRepository.save(invitation);

        // when
        boolean exists = invitationRepository.existsByPaperAndReceiverAndStatus(paper, receiver, InvitationStatus.ACCEPTED);

        // then
        assertTrue(exists);
    }


    @Test
    @DisplayName("findByPaperAndStatus 테스트")
    public void testFindByPaperAndStatus() {
        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);

        Invitation invitation1 = Invitation.builder()
                .paper(paper)
                .status(InvitationStatus.ACCEPTED)
                .build();

        Invitation invitation2 = Invitation.builder()
                .paper(paper)
                .status(InvitationStatus.ACCEPTED)
                .build();

        List<Invitation> invitations = Arrays.asList(invitation1, invitation2);
        invitationRepository.saveAll(invitations);

        // when
        List<Invitation> results = invitationRepository.findByPaperAndStatus(paper, InvitationStatus.ACCEPTED);

        //then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.contains(invitation1));
        assertTrue(results.contains(invitation2));
    }

    @Test
    @DisplayName("findAllByReceiverAndStatus 테스트")
    public void testFindAllByReceiverAndStatus() {
        // given
        User receiver = User.userDetail()
                .nickname("testNickname")
                .email("receiver@gmail.com")
                .build();
        userRepository.save(receiver);

        Invitation invitation1 = Invitation.builder()
                .status(InvitationStatus.ACCEPTED)
                .receiver(receiver)
                .build();

        Invitation invitation2 = Invitation.builder()
                .status(InvitationStatus.ACCEPTED)
                .receiver(receiver)
                .build();

        List<Invitation> invitations = Arrays.asList(invitation1, invitation2);
        invitationRepository.saveAll(invitations);


        // when
        List<Invitation> results = invitationRepository.findAllByReceiverAndStatus(receiver, InvitationStatus.ACCEPTED);

        // then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("findByPaper 테스트")
    public void testFindByPaper() {
        Paper paper = Paper.builder()
                .title("Paper")
                .isPublic(IsPublic.PUBLIC)
                .build();
        paperRepository.save(paper);

        Invitation invitation1 = Invitation.builder()
                .paper(paper)
                .status(InvitationStatus.ACCEPTED)
                .build();

        Invitation invitation2 = Invitation.builder()
                .paper(paper)
                .status(InvitationStatus.ACCEPTED)
                .build();

        List<Invitation> invitations = Arrays.asList(invitation1, invitation2);
        invitationRepository.saveAll(invitations);

        // when
        List<Invitation> results = invitationRepository.findByPaper(paper);

        //then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.contains(invitation1));
        assertTrue(results.contains(invitation2));
    }

}