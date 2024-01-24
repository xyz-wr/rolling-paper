package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 계정이 존재하지 않습니다."));

        if (findUser != null) {
            PrincipalDetails principalDetails = new PrincipalDetails(findUser);
            return principalDetails;
        }

        return null;
    }
}
