package com.wrbread.roll.rollingpaper.model.entity;

import com.wrbread.roll.rollingpaper.model.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Column(unique = true)
    private String codename;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paper> papers = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    private String provider;

    private String providerId;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private ProfileImg profileImg;

    @Column(nullable = false)
    private int writeCount = 3;

    private boolean isSubscriber;

    @Builder(builderClassName = "UserDetail",builderMethodName = "userDetail")
    public User(Long id, String nickname, String email, String password, String codename, Role role, ProfileImg profileImg) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.codename = codename;
        this.profileImg = profileImg;
    }

    @Builder(builderClassName = "OAuth2User",builderMethodName = "oAuth2User")
    public User(Long id, String nickname, String email, String password, String codename, Role role, String provider, String providerId, ProfileImg profileImg) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.codename = codename;
        this.provider = provider;
        this.providerId = providerId;
        this.profileImg = profileImg;
    }

    public void decreaseWriteCount() {
        if(this.writeCount > 0) {
            this.writeCount--;
        }
    }

    public void purchaseSubscription() {
        this.isSubscriber = true;
    }

    public void updateUser(String nickname, ProfileImg profileImg) {
        this.nickname = nickname;
        this.profileImg = profileImg;
    }

}
