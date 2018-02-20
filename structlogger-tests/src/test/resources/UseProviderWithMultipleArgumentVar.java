import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.StructLogger;

public class UseProviderWithMultipleArgumentVar {

    @LoggerContext(context = ContextProviderMultipleArgumentVar.class)
    private static StructLogger<ContextProviderMultipleArgumentVar> defaultLog = new StructLogger<>(
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
