package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AuthDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class LoginDto {
        private String email;
        private String password;

        @Builder
        public LoginDto(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class JoinDto {
        private Long id;

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        private String nickname;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "이메일 형식으로 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        @Size(min=8, max=16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요")
        private String password;

        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        private String passwordCheck;

        public User toEntity(String codename, String password) {
            return User.userDetail()
                    .id(id)
                    .nickname(nickname)
                    .email(email)
                    .password(password)
                    .codename(codename)
                    .role(Role.USER)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class TokenDto {
        private String accessToken;
        private String refreshToken;

        public TokenDto(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserDto {
        private Long id;
        private String nickname;
        private String codename;
        private String email;
        private ProfileImgDto profileImgDto;
        private Long profileImgId;

        public UserDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.codename = user.getCodename();
            this.profileImgDto = new ProfileImgDto(user.getProfileImg());
            this.profileImgId = user.getProfileImg().getId();
        }
    }
}
