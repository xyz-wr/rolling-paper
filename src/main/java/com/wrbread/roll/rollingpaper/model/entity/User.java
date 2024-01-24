package com.wrbread.roll.rollingpaper.model.entity;

import com.wrbread.roll.rollingpaper.model.enums.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public User(String nickname, String email, String password, String codename, Role role) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.codename = codename;
    }
}
