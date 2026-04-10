package com.fbp.engine.app.cli.exception;

import com.fbp.engine.core.exception.EngineException;

public class CommandNotFoundException extends EngineException {
    public CommandNotFoundException(String token) {
        super("알 수 없는 명령어입니다: " + token);
    }
}
