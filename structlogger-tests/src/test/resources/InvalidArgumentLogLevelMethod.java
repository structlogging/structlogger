import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;
import cz.muni.fi.DefaultContext;

public class InvalidArgumentLogLevelMethod {

    @LoggerContext(context = DefaultContext.class)
    private static EventLogger<DefaultContext> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        final String value = "Should not compile {} {}";

        defaultLog.info(value)
                .varDouble(1.2)
                .varBoolean(false)
                .log();
    }
}
