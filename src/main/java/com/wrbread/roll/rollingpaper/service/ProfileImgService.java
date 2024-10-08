package com.wrbread.roll.rollingpaper.service;

import com.wrbread.roll.rollingpaper.model.dto.ProfileImgDto;
import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.repository.ProfileImgRepository;
import com.wrbread.roll.rollingpaper.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.net.URL;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ProfileImgService {
    private final ProfileImgRepository profileImgRepository;

    private final S3Service s3Service;

    private final String imgFolder = "profileImg";

    private final String DEFAULT_PROFILE_IMG = "https://rollingpaper.s3.ap-northeast-2.amazonaws.com/default/default_profileImg.png";


    /** 이미지 파일 업로드 */
    public ProfileImg saveProfileImg(User user, MultipartFile file) throws Exception {
        String oriImgNm = file.getOriginalFilename();
        String imgUrl = "";

        ProfileImgDto profileImgDto = new ProfileImgDto();
        //파일 업로드
        if (!StringUtils.isEmpty(oriImgNm)) {
            profileImgDto.setOriImgNm(oriImgNm);
            imgUrl = s3Service.uploadS3(oriImgNm, imgFolder, file.getBytes());
        } else {
            imgUrl = DEFAULT_PROFILE_IMG;
        }

        //상품 이미지 정보 저장
        profileImgDto.setImgUrl(imgUrl);

        ProfileImg profileImg = profileImgDto.toEntity();
        profileImg.addUser(user);
        profileImgRepository.save(profileImg);

        return profileImg;
    }


    /** 이미지 파일 삭제 */
    public void deleteProfileImg(Long imgId) throws Exception {
        ProfileImg profileImg = profileImgRepository.findById(imgId)
                .orElseThrow(() ->new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        String imgUrl = profileImg.getImgUrl();

        if (!imgUrl.equals(DEFAULT_PROFILE_IMG)) {
            URL url = new URL(imgUrl);
            String path = url.getPath().substring(1);
            s3Service.deleteS3(path);
        }
        profileImgRepository.delete(profileImg);
    }
}
