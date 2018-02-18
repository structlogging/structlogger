/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package cz.muni.fi;

import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import org.openjdk.jmh.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(5)
public class Slf4jToFileBenchmark {

    @LoggerContext(context = DefaultContext.class)
    private static EventLogger<DefaultContext> structLogger = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger(
                            Slf4jToFileBenchmark.class.getSimpleName()
                    )
            )
    );

    private static Logger logger = LoggerFactory.getLogger(Slf4jToFileBenchmark.class.getSimpleName() + "2");

    @LoggerContext(context = DefaultContextWithoutParametrization.class)
    private static EventLogger<DefaultContext> structLoggerNoMessageParametrization = new EventLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger(
                            Slf4jToFileBenchmark.class.getSimpleName() + "3"
                    )
            )
    );

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger1CallWithParametrizedMessage() {
        structLogger.info("Event with double={} and boolean={}")
                .varDouble(1.2)
                .varBoolean(false)
                .log();
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger10CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger20CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(2);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger30CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(3);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger40CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger50CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(5);
    }


    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger60CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(6);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger70CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(7);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger80CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(8);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger90CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(9);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger100CallsWithParametrizedMessage() {
        structLogger10CallsWithParametrizedMessage(10);
    }

    // structured logging with no message parametrization

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging1Call() {
        structLoggerNoMessageParametrization.info("Event with double and boolean")
                .varDouble(1.2)
                .varBoolean(false)
                .log();
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging10Calls() {
        structLogger10Calls(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging20Calls() {
        structLogger10Calls(2);
    }


    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging30Calls() {
        structLogger10Calls(3);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging40Calls() {
        structLogger10Calls(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging50Calls() {
        structLogger10Calls(5);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging60Calls() {
        structLogger10Calls(6);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging70Calls() {
        structLogger10Calls(7);
    }


    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging80Calls() {
        structLogger10Calls(8);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging90Calls() {
        structLogger10Calls(9);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging100Calls() {
        structLogger10Calls(10);
    }


    //not structured logging

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging1Call() {
        logger.info("log double={} and boolean={}", 1.2, false);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging10Calls() {
        slf4jLog10Calls(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging20Calls() {
        slf4jLog10Calls(2);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging30Calls() {
        slf4jLog10Calls(3);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging40Calls() {
        slf4jLog10Calls(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging50Calls() {
        slf4jLog10Calls(5);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging60Calls() {
        slf4jLog10Calls(6);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging70Calls() {
        slf4jLog10Calls(7);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging80Calls() {
        slf4jLog10Calls(8);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging90Calls() {
        slf4jLog10Calls(9);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging100Calls() {
        slf4jLog10Calls(10);
    }

    private void structLogger10CallsWithParametrizedMessage(int number) {
        for(int i = 0; i < number; i++) {
            structLogger.info("Event with double={} and boolean={}")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .log();

            structLogger.info("Event with integer={}")
                    .varInt(1)
                    .log();

            structLogger.info("Event with double={} and boolean={} and string={}")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .varString("test string value")
                    .log();

            structLogger.info("Event with double={} and boolean={} and long={} an string={}")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .varLong(1)
                    .varString("test string value")
                    .log();

            structLogger.info("Event with double={}")
                    .varDouble(1.2)
                    .log();

            structLogger.error("Event with string={} and long={} and boolean={}")
                    .varString("super error")
                    .varLong(42)
                    .varBoolean(true)
                    .log();

            structLogger.warn("Event with boolean={} and boolean={} and integer={}")
                    .varBoolean(true)
                    .varBoolean(false)
                    .varInt(1)
                    .log();

            structLogger.error("Event with no parameters")
                    .log();


            structLogger.info("Event with string={} and string={}")
                    .varString("string value 1")
                    .varString("string value 2")
                    .log();

            structLogger.info("Event with string={} and string={} and long={} and boolean={}")
                    .varString("string value 1")
                    .varString("string value 2")
                    .varLong(1L)
                    .varBoolean(true)
                    .log();
        }
    }

    private void structLogger10Calls(int number) {
        for(int i = 0; i < number; i++) {
            structLoggerNoMessageParametrization.info("Event with double and boolean")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .log();

            structLoggerNoMessageParametrization.info("Event with integer")
                    .varInt(1)
                    .log();

            structLoggerNoMessageParametrization.info("Event with double and boolean and string")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .varString("test string value")
                    .log();

            structLoggerNoMessageParametrization.info("Event with double and boolean and long and string")
                    .varDouble(1.2)
                    .varBoolean(false)
                    .varLong(1)
                    .varString("test string")
                    .log();

            structLoggerNoMessageParametrization.info("Event with double")
                    .varDouble(1.2)
                    .log();

            structLoggerNoMessageParametrization.error("Event with string and long and boolean")
                    .varString("super error")
                    .varLong(42)
                    .varBoolean(true)
                    .log();

            structLoggerNoMessageParametrization.warn("Event with two booleans and integer")
                    .varBoolean(true)
                    .varBoolean(false)
                    .varInt(1)
                    .log();

            structLoggerNoMessageParametrization.error("Event with no parameters")
                    .log();

            structLoggerNoMessageParametrization.info("Event with string and string")
                    .varString("string value 1")
                    .varString("string value 2")
                    .log();

            structLoggerNoMessageParametrization.info("Event with string and string and long and boolean value")
                    .varString("string value 1")
                    .varString("string value 2")
                    .varLong(1L)
                    .varBoolean(true)
                    .log();
        }
    }

    private void slf4jLog10Calls(int number) {
        for(int i = 0; i < number; i++) {
            logger.info("log string={} and boolean={}", 1.2, false);

            logger.info("log integer={}", 1);

            logger.info("log double={} and boolean={} and string={}", 1.2, false, "ahojky");

            logger.info("log double={} and boolean={} and integer={} and string={}", 1.2, false, 1, "tudu");

            logger.info("log double={}", 1.2);

            logger.error("log integer={} and boolean={}", 42, true);

            logger.warn("log boolean={} and boolean={} and integer={}", true, false, 1);

            logger.error("log with no parameters");

            logger.info("log with string={} and string={}", "string value 1", "string value 2");

            logger.info("log with string={} and string={} and long={} and boolean={}", "string value 1", "string value 2", 1L, true);
        }
    }
}
