package com.fbp.engine.cli.exception;

import com.fbp.engine.exception.EngineException;

public class InvalidCommandArgumentsException extends EngineException {
    public InvalidCommandArgumentsException(String usage) {
        super("잘못된 명령어 인자입니다. 사용법: " + usage);
    }
}
