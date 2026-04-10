package com.fbp.engine.node.impl;

import com.fbp.engine.edge.connection.Connection;
import com.fbp.engine.edge.connection.LocalConnection;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SplitNodeTest {

    @Test
    @DisplayName("onProcess: 만족 메시지가 match 포트로 전달됨(1)")
    void testOnProcess_MatchPort() {
        // Given
        SplitNode splitNode = new SplitNode("splitNode1", "testKey", 5);
        Message message = Message.of(Map.of("testKey", 10));

        Connection matchConnection = new LocalConnection("match-connection");
        splitNode.getOutputPort("match").connect(matchConnection);

        // When
        splitNode.process(new PortMessage("in", message));
        Message result = matchConnection.poll();

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals((Integer) 10, result.get("testKey"))
        );
    }

    @Test
    @DisplayName("onProcess: 미달 메시지가 mismatch 포트로 전달됨(2)")
    void testOnProcess_MismatchPort() {
        // Given
        SplitNode splitNode = new SplitNode("splitNode1", "testKey", 5);
        Message message = Message.of(Map.of("testKey", 3));

        Connection mismatchConnection = new LocalConnection("mismatch-connection");
        splitNode.getOutputPort("mismatch").connect(mismatchConnection);

        // When
        splitNode.process(new PortMessage("in", message));
        Message result = mismatchConnection.poll();

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals((Integer) 3, result.get("testKey"))
        );
    }

    @Test
    @DisplayName("onProcess: 만족/미달 메시지를 각각 보내면 양쪽 포트에서 각각 수신됨(3)")
    void testOnProcess_MatchAndMismatchPorts() {
        // Given
        SplitNode splitNode = new SplitNode("splitNode1", "testKey", 5);
        Message matchMessage = Message.of(Map.of("testKey", 10));
        Message mismatchMessage = Message.of(Map.of("testKey", 3));

        Connection matchConnection = new LocalConnection("match-connection");
        Connection mismatchConnection = new LocalConnection("mismatch-connection");
        splitNode.getOutputPort("match").connect(matchConnection);
        splitNode.getOutputPort("mismatch").connect(mismatchConnection);

        // When
        splitNode.process(new PortMessage("in", matchMessage));
        Message matchResult = matchConnection.poll();
        splitNode.process(new PortMessage("in", mismatchMessage));
        Message mismatchResult = mismatchConnection.poll();

        // Then
        assertAll(
                () -> assertNotNull(matchResult),
                () -> assertEquals((Integer) 10, matchResult.get("testKey")),
                () -> assertNotNull(mismatchResult),
                () -> assertEquals((Integer) 3, mismatchResult.get("testKey"))
        );
    }

    @Test
    @DisplayName("onProcess: 조건과 같은 값이 match 포트로 전달됨(4)")
    void testOnProcess_EqualToThreshold() {
        // Given
        SplitNode splitNode = new SplitNode("splitNode1", "testKey", 5);
        Message message = Message.of(Map.of("testKey", 5));

        Connection matchConnection = new LocalConnection("match-connection");
        splitNode.getOutputPort("match").connect(matchConnection);

        // When
        splitNode.process(new PortMessage("in", message));
        Message result = matchConnection.poll();

        // Then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals((Integer) 5, result.get("testKey"))
        );
    }
}
