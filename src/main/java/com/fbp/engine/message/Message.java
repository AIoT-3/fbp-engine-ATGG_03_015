package com.fbp.engine.message;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record Message (
        UUID id,
        Map<String, Object> payload,
        Instant timestamp
) {
    public static Message of(Map<String, Object> payload) {
        return new Message(
                UUID.randomUUID(),
                Collections.unmodifiableMap(new HashMap<>(payload)),
                Instant.now()
        );
    }

    public <T> T get(String key) {
        return (T) payload.get(key);
    }

    public Message withEntry(String key, Object value) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.put(key, value);
        return new Message(
                this.id,
                Collections.unmodifiableMap(newPayload),
                this.timestamp
        );
    }

    public boolean hasKey(String key) {
        return payload.containsKey(key);
    }

    public Message withoutkey(String key) {
        Map<String, Object> newPayload = new HashMap<>(this.payload);
        newPayload.remove(key);
        return new Message(
                this.id,
                Collections.unmodifiableMap(newPayload),
                this.timestamp
        );
    }
}
