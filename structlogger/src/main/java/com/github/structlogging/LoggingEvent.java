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

/**
 * All generated logging events should extend this class, this class contains common attributes of all logging events
 * All changes made to this class should be reflected in LogInvocationScanner and POJOService or you risk incorrect behaviour of processor
 */
public class LoggingEvent {
    private final String type;
    private final Long timestamp;
    private final LoggingEventContext context;

    public LoggingEvent(final String message,
                        final String sourceFile,
                        final Long lineNumber,
                        final String type,
                        final Long sid,
                        final String logLevel,
                        final Long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
        this.context = new LoggingEventContext(message, sourceFile, lineNumber, sid, logLevel);
    }

    public String getType() {
        return type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public LoggingEventContext getContext() {
        return context;
    }
}
