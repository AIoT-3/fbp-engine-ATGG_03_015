package com.fbp.engine.cli.exception;

import com.fbp.engine.exception.EngineException;

public class CommandNotFoundException extends EngineException {
    public CommandNotFoundException(String token) {
        super("알 수 없는 명령어입니다: " + token);
    }
}
