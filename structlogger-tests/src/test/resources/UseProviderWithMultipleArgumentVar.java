import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;

public class UseProviderWithMultipleArgumentVar {

    @LoggerContext(context = ContextProviderMultipleArgumentVar.class)
    private static EventLogger<ContextProviderMultipleArgumentVar> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L, 2L)
                .log();
    }
}
