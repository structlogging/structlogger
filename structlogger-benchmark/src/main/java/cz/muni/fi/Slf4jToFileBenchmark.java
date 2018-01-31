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
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
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
    public void structLogger8CallsWithParametrizedMessage() {
        structLogger8CallsWithParametrizedMessage(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger16CallsWithParametrizedMessage() {
        structLogger8CallsWithParametrizedMessage(2);
    }


    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger32CallsWithParametrizedMessage() {
        structLogger8CallsWithParametrizedMessage(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structLogger64CallsWithParametrizedMessage() {
        structLogger8CallsWithParametrizedMessage(8);
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
    public void structuredLogging8Calls() {
        structLogger8Calls(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging16Calls() {
        structLogger8Calls(2);
    }


    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging32Calls() {
        structLogger8Calls(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void structuredLogging64Calls() {
        structLogger8Calls(8);
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
    public void notStructuredLogging8Calls() {
        slf4jLog8Calls(1);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging16Calls() {
        slf4jLog8Calls(2);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging32Calls() {
        slf4jLog8Calls(4);
    }

    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @Benchmark
    public void notStructuredLogging64Calls() {
        slf4jLog8Calls(8);
    }

    private void structLogger8CallsWithParametrizedMessage(int number) {
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
        }
    }

    private void structLogger8Calls(int number) {
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
        }
    }

    private void slf4jLog8Calls(int number) {
        for(int i = 0; i < number; i++) {
            logger.info("log string={} and boolean={}", 1.2, false);

            logger.info("log integer={}", 1);

            logger.info("log double={} and boolean={} and string={}", 1.2, false, "ahojky");

            logger.info("log double={} and boolean={} and integer={} and string={}", 1.2, false, 1, "tudu");

            logger.info("log double={}", 1.2);

            logger.error("log integer={} and boolean={}", 42, true);

            logger.warn("log boolean={} and boolean={} and integer={}", true, false, 1);

            logger.error("log with no parameters");
        }
    }
}
