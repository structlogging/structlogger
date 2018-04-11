import com.github.structlogging.slf4j.Slf4jLoggingCallback;
import com.github.structlogging.annotation.LoggerContext;
import com.github.structlogging.StructLogger;
import com.github.structlogging.DefaultContext;

public class LoggerContextAnnotationParamDiffers {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<AnotherContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.structlogging.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .context("should really not compile")
                .log();
    }
}
