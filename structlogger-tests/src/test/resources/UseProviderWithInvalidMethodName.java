import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.StructLogger;

public class UseProviderWithInvalidMethodName {

    @LoggerContext(context = ContextProviderWithInvalidMethodName.class)
    private static StructLogger<ContextProviderWithInvalidMethodName> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .warnEvent("test string")
                .log("ShouldNotCompile");

    }
}
