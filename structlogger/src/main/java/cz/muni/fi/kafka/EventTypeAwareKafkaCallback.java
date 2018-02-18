package cz.muni.fi.kafka;

import cz.muni.fi.LoggingCallback;
import cz.muni.fi.LoggingEvent;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Callback, which sends logging events to topics based on event types
 */
public class EventTypeAwareKafkaCallback implements LoggingCallback {

    private final Producer<Long, LoggingEvent> producer;

    public EventTypeAwareKafkaCallback(final Producer<Long, LoggingEvent> producer) {
        this.producer = producer;
    }

    @Override
    public void info(final LoggingEvent e) {
        sendEvent(e);
    }

    @Override
    public void warn(final LoggingEvent e) {
        sendEvent(e);
    }

    @Override
    public void debug(final LoggingEvent e) {
        sendEvent(e);
    }

    @Override
    public void error(final LoggingEvent e) {
        sendEvent(e);
    }

    @Override
    public void trace(final LoggingEvent e) {
        sendEvent(e);
    }

    @Override
    public void audit(final LoggingEvent e) {
        sendEvent(e);
    }

    private void sendEvent(LoggingEvent e) {
        long time = System.currentTimeMillis();

        final ProducerRecord<Long, LoggingEvent> record =
                new ProducerRecord<>(e.getType(), time, e);

        producer.send(record);
    }
}
