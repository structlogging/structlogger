import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;

public class UseProviderWithLogLevelMethodOverriden {

    @LoggerContext(context = ContextProviderWithLogLevelMethodOverriden.class)
    private static StructLogger<ContextProviderWithLogLevelMethodOverriden> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .log("ShouldNotCompile");

    }
}
