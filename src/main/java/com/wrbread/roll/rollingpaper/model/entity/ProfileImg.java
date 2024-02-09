package com.wrbread.roll.rollingpaper.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class ProfileImg extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oriImgNm; //원본 이미지 파일명

    private String imgUrl; //이미지 경로

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public ProfileImg(String oriImgNm, String imgUrl, User user) {
        this.oriImgNm = oriImgNm;
        this.imgUrl = imgUrl;
        this.user = user;
    }

    public void updateProductImg(String oriImgNm, String imgUrl) {
        this.oriImgNm = oriImgNm;
        this.imgUrl = imgUrl;
    }
}
