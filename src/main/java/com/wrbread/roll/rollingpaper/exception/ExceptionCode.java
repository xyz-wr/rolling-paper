package com.wrbread.roll.rollingpaper.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ExceptionCode {
    // user 관련
    NICKNAME_EXISTS(400, "이미 존재하는 닉네임입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTS(400, "이미 존재하는 이메일입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(400, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(404, "회원 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NO_PERMISSION_ACCESS(403, "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // paper
    PAPER_NOT_FOUND(404, "해당 롤링 페이퍼가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // message
    MESSAGE_NOT_FOUND(404, "해당 메시지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // invitation
    NO_INVITATION_SELF(400, "자기 자신은 초대할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_SEND_INVITATION_TO_PUBLIC(400, "전체 공개는 초대장을 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    ONLY_CREATOR_CAN_SEND_INVITATION(400, "롤링 페이퍼를 생성한 유저만 초대장 발송이 가능합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ACCEPTED_INVITATION(400, "이미 초대를 허락한 유저입니다.", HttpStatus.BAD_REQUEST),
    INVITATION_ALREADY_HANDLED(400, "이미 수락하거나 거절한 초대장입니다.", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_FOUND(404, "해당 초대장이 존재하지 않습니다.", HttpStatus.NOT_FOUND);

    @Getter
    private final int status;
    @Getter
    private final String message;
    @Getter
    private final HttpStatus httpStatus;

    ExceptionCode(int code, String message, HttpStatus httpStatus) {
        this.status = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
