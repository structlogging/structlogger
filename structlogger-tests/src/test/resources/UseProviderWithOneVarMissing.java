import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.StructLogger;

public class UseProviderWithOneVarMissing {

    @LoggerContext(context = ContextProviderOneVarMissing.class)
    private static StructLogger<ContextProviderOneVarMissing> defaultLog = new StructLogger<>(
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
