package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.ProfileImg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileImgDto {
    private Long id;
    private String oriImgNm;
    private String imgUrl;

    public ProfileImg toEntity() {
        return ProfileImg.builder()
                .oriImgNm(oriImgNm)
                .imgUrl(imgUrl)
                .build();
    }

    public ProfileImgDto(ProfileImg profileImg) {
        this.id = profileImg.getId();
        this. oriImgNm = profileImg.getOriImgNm();
        this.imgUrl = profileImg.getImgUrl();
    }
}
