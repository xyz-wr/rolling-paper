package com.wrbread.roll.rollingpaper.auth.oauth;

import com.wrbread.roll.rollingpaper.auth.PrincipalDetails;
import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.Role;
import com.wrbread.roll.rollingpaper.repository.ProfileImgRepository;
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

    private final String DEFAULT_PROFILE_IMG = "https://rolling-paper.s3.ap-northeast-2.amazonaws.com/default/default_profileImg.png";

    private final ProfileImgRepository profileImgRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if(provider.equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(provider.equals("naver")){
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else if(provider.equals("kakao")){
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        String providerId = oAuth2UserInfo.getProviderId();
        String nickname = provider + "_" +providerId;

        String codename = randomUtil.generateRandomString();

        String email = oAuth2UserInfo.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        if(optionalUser.isEmpty()) {
            user = User.oAuth2User()
                    .codename(codename)
                    .nickname(nickname)
                    .email(email)
                    .role(Role.USER)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);

            ProfileImg profileImg = ProfileImg
                    .builder()
                    .user(user)
                    .imgUrl(DEFAULT_PROFILE_IMG)
                    .build();

            profileImg.addUser(user);

            profileImgRepository.save(profileImg);
        } else {
            user = optionalUser.get();
        }

        return new PrincipalDetails(user, oAuth2UserInfo);

    }
}