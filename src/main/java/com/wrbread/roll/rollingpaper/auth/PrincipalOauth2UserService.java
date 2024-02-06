package com.wrbread.roll.rollingpaper.auth;

import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.Role;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import com.wrbread.roll.rollingpaper.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    private final RandomUtil randomUtil;

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        String email = provider + "_" +providerId;
        String codename = randomUtil.generateRandomString();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if(optionalUser.isEmpty()) {
            user = User.oAuth2User()
                    .codename(codename)
                    .email(email)
                    .nickname(oAuth2User.getAttribute("name"))
                    .role(Role.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());

    }
}
