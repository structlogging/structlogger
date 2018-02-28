import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;

public class UseProviderWithMultipleArgumentVar {

    @LoggerContext(context = ContextProviderMultipleArgumentVar.class)
    private static StructLogger<ContextProviderMultipleArgumentVar> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L, 2L)
                .log();
    }
}
