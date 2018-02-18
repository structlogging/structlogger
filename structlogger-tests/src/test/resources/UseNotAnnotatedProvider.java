import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;

public class UseNotAnnotatedProvider {

    @LoggerContext(context = ContextProviderNotAnnotated.class)
    private static EventLogger<ContextProviderNotAnnotated> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varDouble(1.2)
                .varBoolean(false)
                .log("ShouldNotCompile");

    }
}
