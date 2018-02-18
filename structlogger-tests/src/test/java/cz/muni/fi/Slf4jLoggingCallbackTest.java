package cz.muni.fi;

import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import cz.muni.fi.annotation.LoggerContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Slf4jLoggingCallbackTest {

    private Logger logger = mock(Logger.class);

    @LoggerContext(context = TestContext.class)
    private EventLogger<TestContext> structLogger;

    @Before
    public void setUp() throws Exception {
        logger = mock(Logger.class);
        structLogger = new EventLogger<>(
                new Slf4jLoggingCallback(logger)
        );
    }

    @Test
    public void testLoggerIsCalled() throws Exception {
        structLogger
                .info("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        structLogger
                .warn("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        structLogger
                .error("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        structLogger
                .debug("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        structLogger
                .trace("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        structLogger
                .audit("test logger is called")
                .varInt(1)
                .varString("ahoj")
                .log();

        verify(logger, times(6));
    }
}
