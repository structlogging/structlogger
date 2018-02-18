package cz.muni.fi;

import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.kafka.EventTypeAwareKafkaCallback;
import cz.muni.fi.kafka.LoggingEventJsonSerializer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EventTypeAwareKafkaCallbackTest {

    private MockProducer<Long, LoggingEvent> mockProducer;

    @LoggerContext(context = TestContext.class)
    private EventLogger<TestContext> testLogger;

    @Before
    public void setUp() {
        mockProducer = new MockProducer<>(
                true,
                new LongSerializer(),
                new LoggingEventJsonSerializer()
        );

        final EventTypeAwareKafkaCallback kafkaCallback = new EventTypeAwareKafkaCallback(mockProducer);

        testLogger = new EventLogger<>(kafkaCallback);
    }

    @Test
    public void testEventIsSerializedAndSentUsingProducer() {
        testLogger.info("Event sent via kafka")
                .varInt(10)
                .varString("string value")
                .log("KafkaEvent1");

        testLogger.info("Event sent via kafka")
                .varInt(10)
                .varString("string value")
                .log("KafkaEvent2");

        assertThat(mockProducer.history().size(), is(2));

        final ProducerRecord<Long, LoggingEvent> e0 = mockProducer.history().get(0);
        final ProducerRecord<Long, LoggingEvent> e1 = mockProducer.history().get(1);

        assertThat(e0.topic(), is("KafkaEvent1"));
        assertThat(e1.topic(), is("KafkaEvent2"));

        final LoggingEvent loggingEventE0 = e0.value();
        final LoggingEvent loggingEventE1 = e1.value();

        assertThat(loggingEventE0.getType(), is("KafkaEvent1"));
        assertThat(loggingEventE1.getType(), is("KafkaEvent2"));
    }

    @Test
    public void testEventIsSerializedAndSentUsingProducerToSameTopic() {
        final String topicName = "SAME_TOPIC";
        testLogger = new EventLogger<>(new EventTypeAwareKafkaCallback(mockProducer, e -> topicName));

        testLogger.info("Event sent via kafka")
                .varInt(10)
                .log("KafkaEvent3");

        testLogger.info("Event sent via kafka")
                .varInt(10)
                .log("KafkaEvent4");

        assertThat(mockProducer.history().size(), is(2));

        final ProducerRecord<Long, LoggingEvent> e0 = mockProducer.history().get(0);
        final ProducerRecord<Long, LoggingEvent> e1 = mockProducer.history().get(1);

        assertThat(e0.topic(), is(topicName));
        assertThat(e1.topic(), is(topicName));

        final LoggingEvent loggingEventE0 = e0.value();
        final LoggingEvent loggingEventE1 = e1.value();

        assertThat(loggingEventE0.getType(), is("KafkaEvent3"));
        assertThat(loggingEventE1.getType(), is("KafkaEvent4"));
    }
}
