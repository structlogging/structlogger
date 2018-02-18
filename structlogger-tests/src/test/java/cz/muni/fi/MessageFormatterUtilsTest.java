package cz.muni.fi;

import cz.muni.fi.utils.MessageFormatterUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class MessageFormatterUtilsTest {

    @Test
    public void testFormat() {
       final String result = MessageFormatterUtils.format("some {} test {} string {}", "value", 1, true);

       assertThat(result, is("some value test 1 string true"));
    }
}
