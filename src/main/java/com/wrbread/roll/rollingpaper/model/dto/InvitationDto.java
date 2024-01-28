package com.wrbread.roll.rollingpaper.model.dto;

import com.wrbread.roll.rollingpaper.model.entity.Invitation;
import com.wrbread.roll.rollingpaper.model.entity.Paper;
import com.wrbread.roll.rollingpaper.model.entity.User;
import com.wrbread.roll.rollingpaper.model.enums.InvitationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class InvitationDto {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Request {
        private String recEmail;

        public Invitation toEntity(User sender, User receiver, Paper paper, InvitationStatus status) {
            return Invitation.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .paper(paper)
                    .status(status)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Response {
        private Long id;

        private Long paperId;

        private InvitationStatus status;

        private String title;

        private String senNickname;

        public Response(Invitation invitation) {
            this.id = invitation.getId();
            this.paperId = invitation.getPaper().getId();
            this.status = invitation.getStatus();
            this.title = invitation.getPaper().getTitle();
            this.senNickname = invitation.getSender().getNickname();
        }

    }
}
