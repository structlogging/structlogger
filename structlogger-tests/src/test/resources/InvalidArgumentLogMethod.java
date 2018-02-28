import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;
import com.github.structlogging.DefaultContext;

public class InvalidArgumentLogMethod {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
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
