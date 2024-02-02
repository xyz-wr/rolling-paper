package com.wrbread.roll.rollingpaper.model.entity;

import com.wrbread.roll.rollingpaper.model.dto.PaperDto;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "papers")
@NoArgsConstructor
public class Paper extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String title;

    @Enumerated(EnumType.STRING)
    private IsPublic isPublic;

    @OneToMany(mappedBy = "paper", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public Paper(User user, Long id, String title, IsPublic isPublic) {
        this.user = user;
        this.id = id;
        this.title = title;
        this.isPublic = isPublic;
    }

    public void updatePaper(PaperDto paperDto) {

        this.title = paperDto.getTitle();
    }
}