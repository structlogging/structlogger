/**
 * Copyright © 2018, Ondrej Benkovsky
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
package com.github.tantalor93;

import com.github.tantalor93.slf4j.Slf4jLoggingCallback;
import com.github.tantalor93.annotation.LoggerContext;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class Slf4jLoggingCallbackTest {

    private Logger logger = mock(Logger.class);

    @LoggerContext(context = TestContext.class)
    private StructLogger<TestContext> structLogger;

    @Before
    public void setUp() {
        logger = mock(Logger.class);
        structLogger = new StructLogger<>(
                new Slf4jLoggingCallback(logger)
        );
    }

    @Test
    public void testLoggerIsCalled() {
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
