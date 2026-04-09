package com.fbp.engine.runner.stage1;

import com.fbp.engine.engine.FlowEngine;
import com.fbp.engine.flow.Flow;
import com.fbp.engine.node.impl.*;

public class A1S904 {
    public static void main(String[] args) {
        FlowEngine engine = new FlowEngine();

        Flow tempMonitoringFlow = new Flow("tempMonitoringFlow")
                .addNode(new TimerNode("timer", 1000))
                .addNode(new TemperatureSensorNode("tempSensor", 15, 45))
                .connect("timer", "out", "tempSensor", "trigger")
                .addNode(new ThresholdFilterNode("thresholdFilter", "temperature", 30))
                .connect("tempSensor", "out", "thresholdFilter", "in")
                .addNode(new AlertNode("alert"))
                .connect("thresholdFilter", "alert", "alert", "in")
                .addNode(new LogNode("logger"))
                .connect("thresholdFilter", "normal", "logger", "in");

        engine.register(tempMonitoringFlow);
        engine.startFlow("tempMonitoringFlow");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            engine.shutdown();
        }
    }
}
