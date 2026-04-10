package com.fbp.engine.engine.task;

import com.fbp.engine.flow.Flow;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import com.fbp.engine.node.NodeExecutionMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowTaskFactoryTest {

    private static class PollingNode extends AbstractNode {
        private PollingNode(String id, boolean withInputPort, boolean withOutputPort) {
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

    private static class SelfDrivenNode extends PollingNode {
        private SelfDrivenNode(String id, boolean withInputPort, boolean withOutputPort) {
            super(id, withInputPort, withOutputPort);
        }

        @Override
        public NodeExecutionMode executionMode() {
            return NodeExecutionMode.SELF_DRIVEN;
        }
    }

    @Test
    @DisplayName("createTasks: SELF_DRIVEN 노드는 polling task를 만들지 않는지")
    void testSelfDrivenNodeDoesNotCreateNodeTask() {
        // Given
        Flow flow = new Flow("self-driven-flow")
                .addNode(new SelfDrivenNode("source", false, true))
                .addNode(new PollingNode("sink", true, false))
                .connect("source", "out", "sink", "in");

        // When
        FlowTasks flowTasks = FlowTaskFactory.createTasks(flow);

        // Then
        assertAll(
                () -> assertEquals(1, flowTasks.nodeTasks().size()),
                () -> assertEquals(1, flowTasks.edgeDispatchTasks().size())
        );
    }
}
