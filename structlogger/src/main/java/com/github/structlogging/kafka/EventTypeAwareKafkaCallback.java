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
package com.github.structlogging.kafka;

import com.github.structlogging.LoggingEvent;
import com.github.structlogging.LoggingCallback;
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
