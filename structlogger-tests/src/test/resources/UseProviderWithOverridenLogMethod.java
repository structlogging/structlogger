import cz.muni.fi.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;
import cz.muni.fi.DefaultContext;

public class UseProviderWithOverridenLogMethod {

    @LoggerContext(context = ContextProviderWithOverridenLogMethod.class)
    private static EventLogger<ContextProviderWithOverridenLogMethod> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .log("ShouldNotCompile");

    }
}
