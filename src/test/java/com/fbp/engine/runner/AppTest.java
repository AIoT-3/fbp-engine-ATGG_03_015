package com.fbp.engine.runner;

import com.fbp.engine.engine.EngineState;
import com.fbp.engine.engine.FlowEngine;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.flow.FlowState;
import com.fbp.engine.message.Message;
import com.fbp.engine.node.impl.CollectorNode;
import com.fbp.engine.node.impl.FileWriterNode;
import com.fbp.engine.node.impl.TemperatureSensorNode;
import com.fbp.engine.node.impl.ThresholdFilterNode;
import com.fbp.engine.node.impl.TimerNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    @Test
    @DisplayName("통합: 엔진 상태, 분기, 파일 기록, 데이터 형식과 범위를 만족하는지(1)(2)(3)(4)(5)(6)(7)")
    void testIntegratedFlow() throws IOException, InterruptedException {
        // Given
        FlowEngine flowEngine = new FlowEngine();
        Path tempFile = Files.createTempFile("fbp-engine-integration", ".log");

        TimerNode timerNode = new TimerNode("timer", 20);
        TemperatureSensorNode temperatureSensorNode = new TemperatureSensorNode("tempSensor", 15.0, 45.0);
        ThresholdFilterNode thresholdFilterNode = new ThresholdFilterNode("thresholdFilter", "temperature", 30.0);
        CollectorNode sensorCollector = new CollectorNode("sensorCollector");
        CollectorNode alertCollector = new CollectorNode("alertCollector");
        CollectorNode normalCollector = new CollectorNode("normalCollector");
        FileWriterNode fileWriterNode = new FileWriterNode("fileWriter", tempFile.toString());

        Flow flow = new Flow("final-integration")
                .addNode(timerNode)
                .addNode(temperatureSensorNode)
                .addNode(thresholdFilterNode)
                .addNode(sensorCollector)
                .addNode(alertCollector)
                .addNode(normalCollector)
                .addNode(fileWriterNode)
                .connect("timer", "out", "tempSensor", "trigger")
                .connect("tempSensor", "out", "thresholdFilter", "in")
                .connect("tempSensor", "out", "sensorCollector", "in")
                .connect("thresholdFilter", "alert", "alertCollector", "in")
                .connect("thresholdFilter", "normal", "normalCollector", "in")
                .connect("thresholdFilter", "normal", "fileWriter", "in");

        flowEngine.register(flow);

        // When
        flowEngine.startFlow("final-integration");
        Thread.sleep(500);
        timerNode.shutdown();
        Thread.sleep(300);
        flowEngine.shutdown();

        List<Message> sensorMessages = sensorCollector.getCollected();
        List<Message> alertMessages = alertCollector.getCollected();
        List<Message> normalMessages = normalCollector.getCollected();
        List<String> fileLines = Files.readAllLines(tempFile);

        // Then
        assertAll(
                () -> assertEquals(EngineState.STOPPED, flowEngine.getState()),
                () -> assertEquals(FlowState.STOPPED, flow.getState()),
                () -> assertTrue(alertMessages.stream().allMatch(message -> ((Double) message.get("temperature")) > 30.0)),
                () -> assertTrue(normalMessages.stream().allMatch(message -> ((Double) message.get("temperature")) <= 30.0)),
                () -> assertEquals(sensorMessages.size(), alertMessages.size() + normalMessages.size()),
                () -> assertEquals(normalMessages.size(), fileLines.size()),
                () -> assertTrue(sensorMessages.stream().allMatch(this::hasSensorDataKeys)),
                () -> assertTrue(sensorMessages.stream().allMatch(this::isTemperatureInRange))
        );
    }

    private boolean hasSensorDataKeys(Message message) {
        return message.hasKey("sensorId")
                && message.hasKey("temperature")
                && message.hasKey("unit");
    }

    private boolean isTemperatureInRange(Message message) {
        Double temperature = message.get("temperature");
        return temperature >= 15.0 && temperature <= 45.0;
    }
}
