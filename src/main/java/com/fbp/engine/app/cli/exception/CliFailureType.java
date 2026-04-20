package com.fbp.engine.app.cli.exception;

import com.fbp.engine.common.exception.FailureType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CliFailureType implements FailureType {
    // Command
    COMMAND_NOT_FOUND("알 수 없는 명령어입니다: %s"),
    INVALID_COMMAND_ARGUMENTS("잘못된 명령어 인자입니다. 사용법: %s");

    private final String messageTemplate;
}
