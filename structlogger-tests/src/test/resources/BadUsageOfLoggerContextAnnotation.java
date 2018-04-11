import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;
import com.github.structlogging.DefaultContext;

public class BadUsageOfLoggerContextAnnotation {

    @LoggerContext(context = DefaultContext.class)
    private Object badField;

    public void test() {
        System.out.println(badField);
    }
}
