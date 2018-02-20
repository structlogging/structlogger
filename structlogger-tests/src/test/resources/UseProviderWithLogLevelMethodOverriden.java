import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.StructLogger;

public class UseProviderWithLogLevelMethodOverriden {

    @LoggerContext(context = ContextProviderWithLogLevelMethodOverriden.class)
    private static StructLogger<ContextProviderWithLogLevelMethodOverriden> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .log("ShouldNotCompile");

    }
}
