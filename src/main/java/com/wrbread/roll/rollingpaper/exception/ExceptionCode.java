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
    CODENAME_NOT_FOUND(404, "코드 네임을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WRITE_COUNT_EXCEEDED(400, "글 작성 횟수를 초과하였습니다.", HttpStatus.BAD_REQUEST),

    // paper
    PAPER_NOT_FOUND(404, "해당 롤링 페이퍼가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    PAPER_TITLE_REQUIRED(400, "제목을 입력해주세요.", HttpStatus.BAD_REQUEST),
    SELECT_PUBLIC_STATUS(400, "공개 여부를 선택해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_TITLE_LENGTH(400,"제목은 1글자 이상 10글자 이하로 작성해주세요.", HttpStatus.BAD_REQUEST),

    // message
    MESSAGE_NOT_FOUND(404, "해당 메시지가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    MESSAGE_NAME_REQUIRED(400, "이름을 입력해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_NAME_LENGTH(400,"이름은 1자 이상 10자 이하로 작성해주세요.", HttpStatus.BAD_REQUEST),
    MESSAGE_CONTENT_REQUIRED(400, "내용을 입력해주세요.", HttpStatus.BAD_REQUEST),
    INVALID_CONTENT_LENGTH(400,"내용은 1자 이상 250자 이하로 작성해주세요.", HttpStatus.BAD_REQUEST),

    // invitation
    CANNOT_SEND_INVITATION_TO_SELF(400, "자기 자신은 초대할 수 없습니다.", HttpStatus.BAD_REQUEST),
    CANNOT_SEND_INVITATION_TO_PUBLIC(400, "전체 공개는 초대장을 보낼 수 없습니다.", HttpStatus.BAD_REQUEST),
    ONLY_CREATOR_CAN_SEND_INVITATION(400, "롤링 페이퍼를 생성한 유저만 초대장 발송이 가능합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ACCEPTED_INVITATION(400, "이미 초대를 허락한 유저입니다.", HttpStatus.BAD_REQUEST),
    INVITATION_ALREADY_HANDLED(400, "이미 수락하거나 거절한 초대장입니다.", HttpStatus.BAD_REQUEST),
    INVITATION_NOT_FOUND(404, "해당 초대장이 존재하지 않습니다.", HttpStatus.NOT_FOUND),

    // likes
    LIKE_NOT_FOUND(404, "해당 좋아요가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    CANNOT_LIKE_OWN_MESSAGE(400, "자신의 메시지에는 좋아요를 누를 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_LIKED_MESSAGE(400, "이미 좋아요를 눌렀습니다.", HttpStatus.BAD_REQUEST),

    // s3
    UNSUPPORTED_FILE_EXTENSION(400, "지원하지 않는 확장자입니다.", HttpStatus.BAD_REQUEST);

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
