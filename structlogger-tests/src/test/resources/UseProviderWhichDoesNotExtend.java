import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;

public class UseProviderWhichDoesNotExtend {

    @LoggerContext(context = ContextProviderNotExtending.class)
    private static StructLogger<ContextProviderNotExtending> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varDouble(1.2)
                .varBoolean(false)
                .log("ShouldNotCompile");

    }
}
