package cz.muni.fi.kafka;

import cz.muni.fi.LoggingCallback;
import cz.muni.fi.LoggingEvent;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.function.Function;

/**
 * Callback, which sends logging events to topics based on event types
 */
public class EventTypeAwareKafkaCallback implements LoggingCallback {

    private final Producer<Long, LoggingEvent> producer;

    private final Function<String, String> eventTypeToTopicMapping;

    /**
     * Constructors kafka logging callback, which sends events to topics same as event types
     * @param producer producer used to send events
     */
    public EventTypeAwareKafkaCallback(final Producer<Long, LoggingEvent> producer) {
        this(producer, e -> e);
    }

    /**
     * Constructs kafka logging callback, which sends events to topics according to eventTypeToTopicMapping
     * @param producer producer used to send events
     * @param eventTypeToTopicMapping function which takes event type and returns topic, where should it be sent
     */
    public EventTypeAwareKafkaCallback(final Producer<Long, LoggingEvent> producer,
                                       final Function<String, String> eventTypeToTopicMapping) {
        this.producer = producer;
        this.eventTypeToTopicMapping = eventTypeToTopicMapping;
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
                new ProducerRecord<>(eventTypeToTopicMapping.apply(e.getType()), time, e);

        producer.send(record);
    }
}
