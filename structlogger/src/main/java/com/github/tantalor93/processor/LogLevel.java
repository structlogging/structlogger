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
package com.github.tantalor93.processor;

/**
 * supported log levels and their corresponding names and method names
 */
public enum LogLevel {
    INFO("INFO", "info", "infoEvent"),
    DEBUG("DEBUG", "debug", "debugEvent"),
    ERROR("ERROR", "error", "errorEvent"),
    TRACE("TRACE", "trace", "traceEvent"),
    AUDIT("AUDIT", "audit", "auditEvent"),
    WARN("WARN", "warn", "warnEvent");

    private final String levelName;
    private final String levelMethodName;
    private final String logEventMethodName;

    LogLevel(final String levelName, final String levelMethodName, final String logEventMethodName) {
        this.levelName = levelName;
        this.levelMethodName = levelMethodName;
        this.logEventMethodName = logEventMethodName;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelMethodName() {
        return levelMethodName;
    }

    public String getLogEventMethodName() {
        return logEventMethodName;
    }
}
