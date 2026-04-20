package com.fbp.engine.core.exception;

import com.fbp.engine.common.exception.FailureType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EngineFailureType implements FailureType {
    // Flow
    FLOW_NOT_FOUND("플로우를 찾을 수 없습니다."),
    FLOW_NOT_FOUND_BY_ID("플로우를 찾을 수 없습니다: %s"),
    EMPTY_FLOW("플로우가 가진 노드가 없습니다."),
    CYCLE_DETECTED("순환 참조가 감지되었습니다."),

    // Node
    NODE_NOT_FOUND("노드를 찾을 수 없습니다: %s"),
    INVALID_NODE_EXECUTION_MODE("Polling node는 InboxNode의 구현체여야 합니다: %s"),
    NODE_INPUT_ENQUEUE_INTERRUPTED("노드 입력 큐 적재 중 인터럽트가 발생했습니다: %s"),
    NODE_INPUT_DEQUEUE_INTERRUPTED("노드 입력 큐 소비 중 인터럽트가 발생했습니다: %s"),

    // Port
    INPUT_PORT_NOT_FOUND("Node [%s]의 입력 포트 [%s]를 찾을 수 없습니다."),
    OUTPUT_PORT_NOT_FOUND("Node [%s]의 출력 포트 [%s]를 찾을 수 없습니다."),

    // Connection
    CONNECTION_DELIVERY_INTERRUPTED("메시지 전달 중 인터럽트가 발생했습니다: %s"),
    CONNECTION_POLL_INTERRUPTED("메시지 폴링 중 인터럽트가 발생했습니다: %s"),

    // File node
    FILE_NODE_OPERATION_FAILED("파일 노드에서 문제가 발생했습니다: %s"),
    FILE_NODE_NOT_INITIALIZED("파일 노드가 초기화되지 않았습니다."),

    // Runtime
    FLOW_RUNTIME_FAILED("플로우 실행 중 task가 비정상 종료되었습니다: %s"),

    // Protocol
    PROTOCOL_CONFIG_INVALID("프로토콜 설정이 올바르지 않습니다."),
    PROTOCOL_CONNECTION_FAILED("프로토콜 연결에 실패했습니다."),
    PROTOCOL_DISCONNECTION_FAILED("프로토콜 연결 해제에 실패했습니다."),
    PROTOCOL_RECONNECT_FAILED("프로토콜 재연결에 실패했습니다."),
    PROTOCOL_MESSAGE_RECEIVE_FAILED("프로토콜 메시지 수신에 실패했습니다."),
    PROTOCOL_MESSAGE_SEND_FAILED("프로토콜 메시지 전송에 실패했습니다.");

    private final String messageTemplate;
}
