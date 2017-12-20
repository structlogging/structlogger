package cz.muni.fi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Audit callback which sends logging event to topics based on event types
 */
public class EventTypeAwareKafkaAuditCallback implements LoggingCallback {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Producer<Long, String> producer;

    public EventTypeAwareKafkaAuditCallback(final String bootstrapServer) {
        producer = createProducer(bootstrapServer);
    }

    @Override
    public void info(final LoggingEvent e) {
        throw new UnsupportedOperationException("info logging not supported");
    }

    @Override
    public void warn(final LoggingEvent e) {
        throw new UnsupportedOperationException("warn logging not supported");

    }

    @Override
    public void debug(final LoggingEvent e) {
        throw new UnsupportedOperationException("debug logging not supported");
    }

    @Override
    public void error(final LoggingEvent e) {
        throw new UnsupportedOperationException("error logging not supported");
    }

    @Override
    public void trace(final LoggingEvent e) {
        throw new UnsupportedOperationException("trace logging not supported");
    }

    @Override
    public void audit(final LoggingEvent e) {

        long time = System.currentTimeMillis();

        try {
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(e.getType(), time, MAPPER.writeValueAsString(e)); //serialize event as string, just for example case, I don't want to implement kafka json serializer

                RecordMetadata metadata = producer.send(record).get();

                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("sent record(key=%s value=%s) " +
                                "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);

        } catch (InterruptedException e1) {
            e1.printStackTrace(); //TODO
        } catch (ExecutionException e1) {
            e1.printStackTrace(); //TODO
        } catch (JsonProcessingException e1) {
            e1.printStackTrace(); //TODO
        } finally {
            producer.flush();
        }

    }

    private static Producer<Long, String> createProducer(final String bootstrap) {
        final Properties props = createProps(bootstrap);
        return new KafkaProducer<>(props);
    }

    private static Properties createProps(final String bootstrap) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrap);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName()); //use string serializer for simplicity
        return props;
    }
}
