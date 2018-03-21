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
 * Main class providing structured logging, parametrized by interface extending {@link VariableContext} class
 */
public final class StructLogger<T extends VariableContext> {

    private final LoggingCallback callback;

    public StructLogger(final LoggingCallback callback) {
        this.callback = callback;
    }


    /**
     * log event on debug level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by debugEvent event with appropriate generated event passed to it
     */
    public T debug(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on info level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by infoEvent event with appropriate generated event passed to it
     */
    public T info(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on error level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by errorEvent event with appropriate generated event passed to it
     */
    public T error(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on warn level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by warnEvent event with appropriate generated event passed to it
     */
    public T warn(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on trace level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by traceEvent event with appropriate generated event passed to it
     */
    public T trace(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on audit level, beginning point of fluent API logging
     * @param message to be logged
     * @return VariableContext placeholder, it returns null, but null is never used in compiled code,
     *         fluent api structured call statement is replaced by auditEvent event with appropriate generated event passed to it
     */
    public T audit(String message) {
        return null; //return null on purpose, calls to this methods are replaced by LogInvocationProcessor to corresponding event method
    }

    /**
     * log event on info level
     * @param e event to log
     */
    public void infoEvent(final LoggingEvent e) {
        callback.info(e);
    }

    /**
     * log event on debug level
     * @param e event to log
     */
    public void debugEvent(final LoggingEvent e) {
        callback.debug(e);
    }

    /**
     * log event on error level
     * @param e event to log
     */
    public void errorEvent(final LoggingEvent e) {
        callback.error(e);
    }

    /**
     * log event on warn level
     * @param e event to log
     */
    public void warnEvent(final LoggingEvent e) {
        callback.warn(e);
    }

    /**
     * log event on trace level
     * @param e event to log
     */
    public void traceEvent(final LoggingEvent e) {
        callback.trace(e);
    }

    /**
     * log audit event
     * @param e event to log
     */
    public void auditEvent(final LoggingEvent e) {
        callback.audit(e);
    }
}
