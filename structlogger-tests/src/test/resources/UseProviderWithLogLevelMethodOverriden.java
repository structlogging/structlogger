import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;

public class UseProviderWithLogLevelMethodOverriden {

    @LoggerContext(context = ContextProviderWithLogLevelMethodOverriden.class)
    private static EventLogger<ContextProviderWithLogLevelMethodOverriden> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .log("ShouldNotCompile");

    }
}
