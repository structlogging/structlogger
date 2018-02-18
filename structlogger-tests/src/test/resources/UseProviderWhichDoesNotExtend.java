import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;

public class UseProviderWhichDoesNotExtend {

    @LoggerContext(context = ContextProviderNotExtending.class)
    private static EventLogger<ContextProviderNotExtending> defaultLog = new EventLogger<>(
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
