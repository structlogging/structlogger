/**
 * Copyright © 2018, Ondrej Benkovsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
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
    private StructLogger<TestContext> testLogger;

    @Before
    public void setUp() {
        mockProducer = new MockProducer<>(
                true,
                new LongSerializer(),
                new LoggingEventJsonSerializer()
        );

        final EventTypeAwareKafkaCallback kafkaCallback = new EventTypeAwareKafkaCallback(mockProducer);

        testLogger = new StructLogger<>(kafkaCallback);
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
        testLogger = new StructLogger<>(new EventTypeAwareKafkaCallback(mockProducer, e -> topicName));

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
