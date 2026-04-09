package com.fbp.engine.node.impl;

import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;

import java.util.HashMap;
import java.util.Map;

public class MergeNode extends AbstractNode {
    private Message pendingMessage1;
    private Message pendingMessage2;

    public MergeNode(String id) {
        super(id);
        addInputPort("in-1");
        addInputPort("in-2");
        addOutputPort("out");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        // 1. 입력 포트 이름에 따라 메시지 저장
        if ("in-1".equals(portMessage.inputPortName())) {
            pendingMessage1 = portMessage.message();
        } else if ("in-2".equals(portMessage.inputPortName())) {
            pendingMessage2 = portMessage.message();
        } else {
            return;
        }

        // 2. 두 메시지가 모두 도착했는지 확인
        if (pendingMessage1 == null || pendingMessage2 == null) {
            return;
        }

        // 3. 메시지 병합 및 출력
        Map<String, Object> mergedPayload = new HashMap<>(pendingMessage1.payload());
        mergedPayload.putAll(pendingMessage2.payload());
        send("out", Message.of(mergedPayload));

        // 4. 병합 후 대기 메시지 초기화
        pendingMessage1 = null;
        pendingMessage2 = null;
    }
}
