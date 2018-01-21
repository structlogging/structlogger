package cz.muni.fi;

import cz.muni.fi.annotation.LoggerContext;

public class KafkaAuditExample {

    @LoggerContext(context = AuditContext.class)
    private static EventLogger<AuditContext> logger = new EventLogger<>(new EventTypeAwareKafkaAuditCallback("localhost:9092"));

    public static void main(String[] args) throws Exception {

        logger.audit("start action")
                .id(1)
                .log("StartAction");

        logger.audit("end action")
                .id(2)
                .log("EndAction");
    }
}
