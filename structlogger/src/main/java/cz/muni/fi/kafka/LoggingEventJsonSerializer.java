package cz.muni.fi.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.muni.fi.LoggingEvent;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Logging Event serializer to json for kafka
 */
public class LoggingEventJsonSerializer implements Serializer<LoggingEvent> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, LoggingEvent loggingEvent) {
        try {
            return MAPPER.writeValueAsBytes(loggingEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unable to serialize event", e);
        }
    }

    @Override
    public void close() {

    }
}
