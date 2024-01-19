package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaperDto {
    private Long id;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(min = 1, max = 10, message = "1자 이상 10자 이하로 작성해주세요.")
    private String title;

    @NotNull(message = "공개 여부는 필수 입력 값입니다.")
    private IsPublic isPublic;

    public Paper toEntity() { //빌더 패턴을 사용해 dto를 엔티티로 변환
        return Paper.builder()
                .id(id)
                .title(title)
                .isPublic(isPublic)
                .build();
    }

    public PaperDto(Paper paper) {
        this.id = paper.getId();
        this.title = paper.getTitle();
        this.isPublic = paper.getIsPublic();
    }
}