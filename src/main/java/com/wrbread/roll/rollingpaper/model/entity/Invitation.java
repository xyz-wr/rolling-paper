package com.wrbread.roll.rollingpaper.model.entity;

import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Invitation extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_id")
    private User sender; // 요청할 유저

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id")
    private User receiver; // 요청받은 유저

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status;

    @Builder
    public Invitation(Long id, User sender, User receiver, Paper paper, InvitationStatus status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.paper = paper;
        this.status = status;
    }

    public void accept() { // 초대장 수락
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() { //초대장 거절
        this.status = InvitationStatus.REJECTED;
    }
}
