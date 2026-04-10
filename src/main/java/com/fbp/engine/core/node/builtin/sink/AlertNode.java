package com.fbp.engine.core.node.builtin.sink;

import com.fbp.engine.core.message.PortMessage;
import com.fbp.engine.core.node.AbstractNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlertNode extends AbstractNode {
    private static final String SENSOR_ID = "sensorId";
    private static final String TEMPERATURE = "temperature";

    public AlertNode(String id) {
        super(id);
        addInputPort("in");
    }

    @Override
    public void onProcess(PortMessage portMessage) {
        String sensorId = portMessage.message().get(SENSOR_ID);
        Double temperature = portMessage.message().get(TEMPERATURE);

        if (sensorId == null || sensorId.isBlank() || temperature == null) {
            log.warn("[경고] 알 수 없는 센서 데이터");
            return;
        }

        log.info("[경고] 센서 {} 온도 {}°C - 임계값 초과!", sensorId, temperature);
    }
}
