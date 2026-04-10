package com.fbp.engine.engine;

import com.fbp.engine.engine.exception.FlowTaskExecutionException;
import com.fbp.engine.flow.runtime.FlowRuntimeState;
import com.fbp.engine.flow.exception.FlowNotFoundException;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.message.Message;
import com.fbp.engine.message.PortMessage;
import com.fbp.engine.node.AbstractNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

class FlowEngineTest {

    private static class SourceNode extends AbstractNode {
        private final CountDownLatch initializeLatch;

        private SourceNode(String id, CountDownLatch initializeLatch) {
            super(id);
            this.initializeLatch = initializeLatch;
            addOutputPort("out");
        }

        @Override
        public void initialize() {
            send("out", Message.of(Map.of("value", getId())));
            if (initializeLatch != null) {
                initializeLatch.countDown();
            }
        }

        @Override
        public void onProcess(PortMessage portMessage) {
        }
    }

    private static class SinkNode extends AbstractNode {
        private final CountDownLatch processLatch;
        private final AtomicInteger processCount = new AtomicInteger();
        private volatile boolean shutdownCalled;

        private SinkNode(String id, CountDownLatch processLatch) {
            super(id);
            this.processLatch = processLatch;
            addInputPort("in");
        }

        @Override
        public void shutdown() {
            shutdownCalled = true;
        }

        @Override
        public void onProcess(PortMessage portMessage) {
            processCount.incrementAndGet();
            if (processLatch != null) {
                processLatch.countDown();
            }
        }
    }

    private static class FailingSinkNode extends AbstractNode {
        private final CountDownLatch processLatch;

        private FailingSinkNode(String id, CountDownLatch processLatch) {
            super(id);
            this.processLatch = processLatch;
            addInputPort("in");
        }

        @Override
        public void onProcess(PortMessage portMessage) {
            if (processLatch != null) {
                processLatch.countDown();
            }
            throw new IllegalStateException("node processing failed");
        }
    }

    @Test
    @DisplayName("생성/register: 초기 상태가 INITIALIZED이고 플로우 등록이 되는지(1)(2)")
    void testConstructorAndRegister() {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        Flow flow = new Flow("test-flow");

        // When
        flowEngine.register(flow);

        // Then
        assertAll(
                () -> assertEquals(FlowEngineState.INITIALIZED, flowEngine.getState()),
                () -> assertTrue(flowEngine.getRuntimes().containsKey("test-flow")),
                () -> assertSame(flow, flowEngine.getRuntimes().get("test-flow").getFlow())
        );
    }

    @Test
    @DisplayName("startFlow/stopFlow: 시작 후 RUNNING, 정지 후 STOPPED인지(3)(6)")
    void testStartAndStopFlow() throws InterruptedException {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        CountDownLatch processLatch = new CountDownLatch(1);
        Flow flow = new Flow("test-flow")
                .addNode(new SourceNode("source", null))
                .addNode(new SinkNode("sink", processLatch))
                .connect("source", "out", "sink", "in");
        flowEngine.register(flow);

        // When
        flowEngine.startFlow("test-flow");
        boolean processed = processLatch.await(1, TimeUnit.SECONDS);
        flowEngine.stopFlow("test-flow");

        // Then
        assertAll(
                () -> assertTrue(processed),
                () -> assertEquals(FlowEngineState.STOPPED, flowEngine.getState()),
                () -> assertEquals(FlowRuntimeState.STOPPED, flowEngine.getRuntimes().get("test-flow").getState()),
                () -> assertFalse(flowEngine.getRuntimes().get("test-flow").isRunning())
        );
    }

    @Test
    @DisplayName("startFlow: 없는 flowId면 예외(4)")
    void testStartFlowWithInvalidId() {
        // Given
        FlowEngine flowEngine = new FlowEngine();

        // When & Then
        assertThrows(FlowNotFoundException.class, () -> flowEngine.startFlow("missing"));
    }

    @Test
    @DisplayName("startFlow: 검증 실패면 시작하지 않음(5)")
    void testStartFlowWithValidationFailure() {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        Flow flow = new Flow("invalid-flow");
        flowEngine.register(flow);

        // When
        flowEngine.startFlow("invalid-flow");

        // Then
        assertAll(
                () -> assertEquals(FlowEngineState.INITIALIZED, flowEngine.getState()),
                () -> assertEquals(FlowRuntimeState.READY, flowEngine.getRuntimes().get("invalid-flow").getState()),
                () -> assertFalse(flowEngine.getRuntimes().get("invalid-flow").isRunning())
        );
    }

    @Test
    @DisplayName("shutdown: 전체 플로우 종료 후 엔진 상태가 STOPPED인지(7)")
    void testShutdown() {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        SinkNode sinkNode = new SinkNode("sink", null);
        Flow flow = new Flow("test-flow")
                .addNode(new SourceNode("source", null))
                .addNode(sinkNode)
                .connect("source", "out", "sink", "in");
        flowEngine.register(flow);
        flowEngine.startFlow("test-flow");

        // When
        flowEngine.shutdown();

        // Then
        assertAll(
                () -> assertEquals(FlowEngineState.STOPPED, flowEngine.getState()),
                () -> assertTrue(sinkNode.shutdownCalled),
                () -> assertTrue(flowEngine.getRuntimes().isEmpty())
        );
    }

    @Test
    @DisplayName("다중 플로우: 하나만 stop해도 나머지는 RUNNING인지(8)")
    void testMultipleFlowsIndependent() {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        Flow flowA = new Flow("flowA")
                .addNode(new SourceNode("sourceA", null))
                .addNode(new SinkNode("sinkA", null))
                .connect("sourceA", "out", "sinkA", "in");
        Flow flowB = new Flow("flowB")
                .addNode(new SourceNode("sourceB", null))
                .addNode(new SinkNode("sinkB", null))
                .connect("sourceB", "out", "sinkB", "in");
        flowEngine.register(flowA);
        flowEngine.register(flowB);
        flowEngine.startFlow("flowA");
        flowEngine.startFlow("flowB");

        // When
        flowEngine.stopFlow("flowA");

        // Then
        assertAll(
                () -> assertEquals(FlowRuntimeState.STOPPED, flowEngine.getRuntimes().get("flowA").getState()),
                () -> assertEquals(FlowRuntimeState.RUNNING, flowEngine.getRuntimes().get("flowB").getState()),
                () -> assertEquals(FlowEngineState.RUNNING, flowEngine.getState())
        );

        flowEngine.shutdown();
    }

    @Test
    @DisplayName("listFlows: 등록된 플로우 목록 조회가 예외 없이 동작하는지(9)")
    void testListFlows() {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        Flow flow = new Flow("test-flow");
        flowEngine.register(flow);

        // When & Then
        assertDoesNotThrow(flowEngine::listFlows);
    }

    @Test
    @DisplayName("task 실패: 런타임이 FAILED로 전이되고 원인을 보관하는지(10)")
    void testRuntimeFailureState() throws InterruptedException {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        CountDownLatch processLatch = new CountDownLatch(1);
        Flow flow = new Flow("failed-flow")
                .addNode(new SourceNode("source", null))
                .addNode(new FailingSinkNode("sink", processLatch))
                .connect("source", "out", "sink", "in");
        flowEngine.register(flow);

        // When
        flowEngine.startFlow("failed-flow");
        boolean processed = processLatch.await(1, TimeUnit.SECONDS);
        boolean failed = waitUntil(() -> flowEngine.getRuntimes().get("failed-flow").getState() == FlowRuntimeState.FAILED,
                1, TimeUnit.SECONDS);

        // Then
        assertAll(
                () -> assertTrue(processed),
                () -> assertTrue(failed),
                () -> assertEquals(FlowRuntimeState.FAILED, flowEngine.getRuntimes().get("failed-flow").getState()),
                () -> assertInstanceOf(FlowTaskExecutionException.class,
                        flowEngine.getRuntimes().get("failed-flow").getLastFailure()),
                () -> assertInstanceOf(IllegalStateException.class,
                        flowEngine.getRuntimes().get("failed-flow").getLastFailure().getCause()),
                () -> assertEquals(FlowEngineState.STOPPED, flowEngine.getState())
        );

        flowEngine.shutdown();
    }

    private boolean waitUntil(BooleanSupplier condition, long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) {
                return true;
            }
            Thread.sleep(10);
        }
        return condition.getAsBoolean();
    }
}
