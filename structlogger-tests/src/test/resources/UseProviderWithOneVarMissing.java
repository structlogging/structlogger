import cz.muni.fi.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.EventLogger;
import cz.muni.fi.DefaultContext;

public class UseProviderWithOneVarMissing {

    @LoggerContext(context = ContextProviderOneVarMissing.class)
    private static EventLogger<ContextProviderOneVarMissing> defaultLog = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L)
                .varString("testik")
                .log();

    }
}
