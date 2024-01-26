package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    public static class SignupDto {
        private Long id;

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
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
            return User.builder()
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
    public static class EmailDto {
        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EmailCheckDto {
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요")
        private String email;

        @NotEmpty(message = "인증 번호를 입력해 주세요")
        private String authKey;

    }
}
