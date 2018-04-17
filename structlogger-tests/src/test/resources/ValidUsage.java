import com.github.structlogging.DefaultContext;
import com.github.structlogging.StructLogger;
import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import org.slf4j.LoggerFactory;

public class ValidUsage {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public static void main(String[] args) {

        int numCached = 0;
        int neededCached = 0;
        long blockId = 0;
        long datanodeUuid = 0;
        String reason = "reason";

        defaultLog.info("Event with double={} and boolean={}")
                .varDouble(1.2)
                .varBoolean(false)
                .log("edu.TestEvent");

        defaultLog.info("Event with double={} and boolean={} and double={} and double={}")
                .varDouble(1.2)
                .varBoolean(true)
                .varDouble(5.6)
                .varDouble(1.0)
                .log();
    }

    private static int someMethod() {
        return 0;
    }
}
