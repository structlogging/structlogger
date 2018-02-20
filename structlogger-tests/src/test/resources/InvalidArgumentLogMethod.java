import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;
import com.github.tantalor93.DefaultContext;

public class InvalidArgumentLogMethod {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        final String value = "some.valid.Value";

        defaultLog.info("Should not compile {} {}")
                .varDouble(1.2)
                .varBoolean(false)
                .log(value);
    }
}
