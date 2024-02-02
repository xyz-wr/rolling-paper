package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.Message;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageDto {
    private Long id;

    private Long paperId;

    private Long paperUserId;

    private Long messageUserId;

    private String email;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 1, max = 10, message = "1자 이상 10자 이하로 작성해주세요.")
    private String name;

    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = 250, message = "1자 이상 250자 이하로 작성해주세요.")
    private String content;

    private int likeCount;

    public Message toEntity(User user, Paper paper) {
        return Message.builder()
                .id(id)
                .user(user)
                .paper(paper)
                .name(name)
                .content(content)
                .build();
    }

    public MessageDto(Message message) {
        this.id = message.getId();
        this.paperId = message.getPaper().getId();
        this.paperUserId = message.getPaper().getUser().getId();
        this.messageUserId = message.getUser().getId();
        this.name = message.getName();
        this.content = message.getContent();
        this.likeCount = message.getLikeCount();
        this.email = message.getUser().getEmail();
    }
}