package com.wrbread.roll.rollingpaper.model.entity;

import com.wrbread.roll.rollingpaper.model.dto.MessageDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 250)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public Message(User user, Paper paper, Long id, String name, String content) {
        this.user = user;
        this.paper = paper;
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public void updateMessage(MessageDto messageDto) {
        this.name = messageDto.getName();
        this.content = messageDto.getContent();
    }
}
