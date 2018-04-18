import com.github.structlogging.DefaultContext;
import com.github.structlogging.StructLogger;
import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import org.slf4j.LoggerFactory;

public class UsageOfContextWithForbiddenName {

    @LoggerContext(context = ContextWithForbiddenName.class)
    private static StructLogger<ContextWithForbiddenName> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public static void main(String[] args) {

        defaultLog.info("Event with double and boolean")
                .context("shouldNotCompile")
                .log("edu.TestEvent");
    }

    private static int someMethod() {
        return 0;
    }
}
