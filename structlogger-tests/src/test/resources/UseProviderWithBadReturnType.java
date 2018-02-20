import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import com.github.tantalor93.StructLogger;

public class UseProviderWithBadReturnType {

    @LoggerContext(context = ContextProviderBadReturnType.class)
    private static StructLogger<ContextProviderBadReturnType> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("com.github.tantalor93.Default")
            )
    );

    public void test() {
        defaultLog.info("Should not compile")
                .varLong(1L);

    }
}
