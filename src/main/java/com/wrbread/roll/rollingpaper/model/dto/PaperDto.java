package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.IsPublic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PaperDto {
    private Long id;

    private Long userId;

    private String email;

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(min = 1, max = 10, message = "1자 이상 10자 이하로 작성해주세요.")
    private String title;

    @NotNull(message = "공개 여부는 필수 입력 값입니다.")
    private IsPublic isPublic;

    private List<MessageDto> messages = new ArrayList<>();

    public Paper toEntity(User user) { //빌더 패턴을 사용해 dto를 엔티티로 변환
        return Paper.builder()
                .id(id)
                .user(user)
                .title(title)
                .isPublic(isPublic)
                .build();
    }

    public PaperDto(Paper paper) {
        this.id = paper.getId();
        this.userId = paper.getUser().getId();
        this.email = paper.getUser().getEmail();
        this.title = paper.getTitle();
        this.isPublic = paper.getIsPublic();
        this.messages = paper.getMessages().stream()
                .map(MessageDto::new)
                .toList();
    }
}