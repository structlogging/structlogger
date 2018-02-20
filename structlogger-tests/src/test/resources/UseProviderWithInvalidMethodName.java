import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;

public class UseProviderWithInvalidMethodName {

    @LoggerContext(context = ContextProviderWithInvalidMethodName.class)
    private static StructLogger<ContextProviderWithInvalidMethodName> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .warnEvent("test string")
                .log("ShouldNotCompile");

    }
}
