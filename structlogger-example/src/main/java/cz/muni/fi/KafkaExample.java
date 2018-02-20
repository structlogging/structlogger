package cz.muni.fi;

import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.kafka.EventTypeAwareKafkaCallback;
import cz.muni.fi.kafka.LoggingEventJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;

import java.util.Properties;

public class KafkaExample {

    @LoggerContext(context = AuditContext.class)
    private static StructLogger<AuditContext> logger = new StructLogger<>(
            new EventTypeAwareKafkaCallback(
                    createProducer("localhost:9092")
            )
    );

    public static void main(String[] args) {

        logger.audit("start action")
                .id(1)
                .log("StartAction");

        logger.audit("end action")
                .id(2)
                .log("EndAction");
    }

    private static Producer<Long, LoggingEvent> createProducer(final String bootstrap) {
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
                LoggingEventJsonSerializer.class.getName());
        return props;
    }
}
