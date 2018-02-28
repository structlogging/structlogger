import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;

public class UseProviderWithInvalidMethodName {

    @LoggerContext(context = ContextProviderWithInvalidMethodName.class)
    private static StructLogger<ContextProviderWithInvalidMethodName> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .warnEvent("test string")
                .log("ShouldNotCompile");

    }
}
