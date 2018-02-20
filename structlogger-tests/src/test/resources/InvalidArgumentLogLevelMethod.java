import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;
import com.github.tantalor93.DefaultContext;

public class InvalidArgumentLogLevelMethod {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        final String value = "Should not compile {} {}";

        defaultLog.info(value)
                .varDouble(1.2)
                .varBoolean(false)
                .log();
    }
}
