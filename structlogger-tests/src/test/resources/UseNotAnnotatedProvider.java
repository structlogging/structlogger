import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;

public class UseNotAnnotatedProvider {

    @LoggerContext(context = ContextProviderNotAnnotated.class)
    private static StructLogger<ContextProviderNotAnnotated> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varDouble(1.2)
                .varBoolean(false)
                .log("ShouldNotCompile");

    }
}
