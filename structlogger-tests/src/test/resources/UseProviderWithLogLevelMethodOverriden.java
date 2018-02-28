import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;

public class UseProviderWithLogLevelMethodOverriden {

    @LoggerContext(context = ContextProviderWithLogLevelMethodOverriden.class)
    private static StructLogger<ContextProviderWithLogLevelMethodOverriden> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .log("ShouldNotCompile");

    }
}
