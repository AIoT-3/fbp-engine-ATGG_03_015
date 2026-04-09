package com.fbp.engine.flow.validation;

import com.fbp.engine.flow.Flow;
import com.fbp.engine.flow.exception.CycleDetectedException;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlowValidatorTest {

    private static class TestNode extends AbstractNode {
        private TestNode(String id, boolean withInputPort, boolean withOutputPort) {
            super(id);
            if (withInputPort) {
                addInputPort("in");
            }
            if (withOutputPort) {
                addOutputPort("out");
            }
        }

        @Override
        public void onProcess(PortMessage portMessage) {
        }
    }

    @Test
    @DisplayName("validate: 빈 Flow면 에러 메시지 반환(8)")
    void testValidateEmptyFlow() {
        // Given
        Flow flow = new Flow("empty-flow");

        // When
        List<String> errors = flow.validate();

        // Then
        assertEquals(1, errors.size());
    }

    @Test
    @DisplayName("validate: 정상 Flow면 빈 리스트 반환(9)")
    void testValidateValidFlow() {
        // Given
        Flow flow = new Flow("valid-flow");
        TestNode sourceNode = new TestNode("source", false, true);
        TestNode targetNode = new TestNode("target", true, false);
        flow.addNode(sourceNode)
                .addNode(targetNode)
                .connect("source", "out", "target", "in");

        // When
        List<String> errors = flow.validate();

        // Then
        assertTrue(errors.isEmpty());
    }

    @Test
    @DisplayName("validate: 순환 참조가 있으면 에러 메시지 반환(12)")
    void testValidateCycle() {
        // Given
        Flow flow = new Flow("cycle-flow");
        TestNode nodeA = new TestNode("A", true, true);
        TestNode nodeB = new TestNode("B", true, true);
        flow.addNode(nodeA)
                .addNode(nodeB)
                .connect("A", "out", "B", "in")
                .connect("B", "out", "A", "in");

        // When
        List<String> errors = flow.validate();

        // Then
        assertAll(
                () -> assertEquals(1, errors.size()),
                () -> assertEquals(new CycleDetectedException().getMessage(), errors.get(0))
        );
    }
}
