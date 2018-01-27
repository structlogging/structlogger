package cz.muni;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import cz.muni.fi.EventLogger;
import cz.muni.fi.LoggingEvent;
import cz.muni.fi.annotation.LoggerContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LogInvocationProcessorTest {

    private ListLoggingCallback callback;

    @LoggerContext(context = TestContext.class)
    private EventLogger<TestContext> defaultLog;

    @Before
    public void setUp() throws Exception {
        callback = new ListLoggingCallback();
        defaultLog = new EventLogger<>(callback);
    }

    @Test
    public void shouldLogEventWithNoNamespace() throws Exception {
        defaultLog.info("test")
                .varInt(1)
                .varString("ahoj")
                .log("NoNamespaceEvent");

        final LoggingEvent testEvent = callback.getLoggingEventList().get(0);

        assertThat(testEvent.getType(), is(equalTo("NoNamespaceEvent")));

        assertThat(testEvent.getClass().getPackage(), is(nullValue()));
        assertThat(testEvent.getClass().getSimpleName(), is(equalTo("NoNamespaceEvent")));
    }

    @Test
    public void shouldLogEventGeneratedEventName() throws Exception {
        defaultLog.info("test generated event name")
                .log();

        final LoggingEvent testEvent = callback.getLoggingEventList().get(0);

        assertThat(testEvent.getType(), is(notNullValue()));

        assertThat(testEvent.getClass().getPackage(), is(notNullValue())); //should use default package for auto generated events
    }

    @Test
    public void shouldLogCorrectEvent() throws Exception {
        defaultLog.info("test")
                .varInt(1)
                .varString("ahoj")
                .log("structlogger.test.TestEvent");

        assertThat(callback.getLoggingEventList(), hasSize(1));

        final LoggingEvent testEvent = callback.getLoggingEventList().get(0);

        assertThat(testEvent.getType(), is(equalTo("structlogger.test.TestEvent")));
        assertThat(testEvent.getLogLevel(), is(equalTo("INFO")));
        assertThat(testEvent.getMessage(), is(equalTo("test")));

        assertThat(testEvent.getClass().getPackage().getName(), is(equalTo("structlogger.test")));
        assertThat(testEvent.getClass().getSimpleName(), is(equalTo("TestEvent")));

        final List<String> eventFields = Arrays.asList(testEvent.getClass().getDeclaredFields()).stream().map(e -> e.getName()).collect(Collectors.toList());
        assertThat(eventFields, containsInAnyOrder(equalTo("varInt"), equalTo("varString")));
    }

    @Test
    public void shouldLogEventWithSequencedFields() throws Exception {
        defaultLog.info("test")
                .varInt(1)
                .varInt(1)
                .varInt(1)
                .varInt(1)
                .log("structlogger.test.TestEvent2");

        assertThat(callback.getLoggingEventList(), hasSize(1));

        final LoggingEvent testEvent = callback.getLoggingEventList().get(0);

        final List<String> eventFields = Arrays.asList(testEvent.getClass().getDeclaredFields()).stream().map(e -> e.getName()).collect(Collectors.toList());
        assertThat(eventFields, containsInAnyOrder(equalTo("varInt"), equalTo("varInt1"), equalTo("varInt2"), equalTo("varInt3")));
    }

    @Test
    public void shouldLogEventWithIncreasingSid() throws Exception {
        defaultLog.info("test")
                  .log("X1");

        defaultLog.info("test")
                  .log("X2");

        defaultLog.info("test")
                  .log("X3");

        defaultLog.info("test")
                   .log("X4");

        final List<LoggingEvent> eventList = callback.getLoggingEventList();
        for (int i = 0; i < eventList.size() - 1; i++) {
            assertThat(eventList.get(i).getSid() + 1, is(equalTo(eventList.get(i + 1).getSid())));
        }
    }

    @Test
    public void shouldLogMultipleEvents() {
        defaultLog.info("test")
                  .varInt(1)
                  .log("structlogger.test.A");

        defaultLog.info("test")
                .varInt(2)
                .log("structlogger.test.B");

        defaultLog.info("test")
                .varInt(3)
                .log("structlogger.test.C");

        defaultLog.info("test")
                .varInt(4)
                .log("structlogger.test.D");

        defaultLog.info("test")
                .varInt(5)
                .log("structlogger.test.E");

        assertThat(callback.getLoggingEventList(), hasSize(5));
    }

    @Test
    public void shouldLogEventsWithCorrectLogLevel() throws Exception {
        defaultLog.info("test")
                  .log("structlogger.test.F");

        defaultLog.warn("test")
                  .log("structlogger.test.G");

        defaultLog.error("test")
                  .log("structlogger.test.H");

        defaultLog.trace("test")
                  .log("structlogger.test.I");

        defaultLog.debug("test")
                  .log("structlogger.test.J");

        defaultLog.audit("test")
                  .log("structlogger.test.K");

        assertThat(callback.getLoggingEventList().stream().map(e -> e.getLogLevel()).collect(Collectors.toList()),
                is(hasItems("INFO", "WARN", "ERROR", "TRACE", "DEBUG", "AUDIT")));
    }
}