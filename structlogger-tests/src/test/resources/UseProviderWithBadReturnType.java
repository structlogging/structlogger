import cz.muni.fi.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;
import cz.muni.fi.DefaultContext;

public class UseProviderWithBadReturnType {

    @LoggerContext(context = ContextProviderBadReturnType.class)
    private static EventLogger<ContextProviderBadReturnType> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L);

    }
}
