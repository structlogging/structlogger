import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;

public class UseProviderWithMultipleArgumentVar {

    @LoggerContext(context = ContextProviderMultipleArgumentVar.class)
    private static StructLogger<ContextProviderMultipleArgumentVar> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L, 2L)
                .log();
    }
}
