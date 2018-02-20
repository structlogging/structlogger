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
package com.github.tantalor93.slf4j;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tantalor93.LoggingEvent;
import com.github.tantalor93.LoggingCallback;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

/**
 * Logging callback which serializes events as string and pass them to SLF4j inside log message
 */
public class Slf4jLoggingCallback implements LoggingCallback {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String AUDIT = "AUDIT";
    private final Logger logger;

    public Slf4jLoggingCallback(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final LoggingEvent e) {
        try {
            logger.info(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void warn(final LoggingEvent e) {
        try {
            logger.warn(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void debug(final LoggingEvent e) {
        try {
            logger.debug(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void error(final LoggingEvent e) {
        try {
            logger.error(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    @Override
    public void trace(final LoggingEvent e) {
        try {
            logger.trace(MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }

    /**
     * This implementation uses INFO level of slf4j and marks these logged messages with AUDIT marker
     */
    @Override
    public void audit(final LoggingEvent e) {
        try {
            logger.info(MarkerFactory.getMarker(AUDIT), MAPPER.writeValueAsString(e));
        } catch (Exception ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }
}
