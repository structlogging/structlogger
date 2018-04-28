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
package com.github.structlogging;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * LoggingCallback implementation which serializes LoggingEvent instances as JSONs and writes them into provided outputstream,
 * each json is ended with newline
 */
public class OutputStreamCallback implements LoggingCallback {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final OutputStream outputStream;

    public OutputStreamCallback(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void info(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    @Override
    public void warn(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    @Override
    public void debug(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    @Override
    public void error(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    @Override
    public void trace(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    @Override
    public void audit(final LoggingEvent e) {
        writeToOutputStream(e);
    }

    private void writeToOutputStream(final LoggingEvent e) {
        try {
            outputStream.write((MAPPER.writeValueAsString(e) + System.lineSeparator()).getBytes());
        } catch (IOException ex) {
            throw new RuntimeException("unable to serialize event", ex);
        }
    }
}
