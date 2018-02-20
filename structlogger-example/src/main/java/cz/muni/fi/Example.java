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
package cz.muni.fi;

import cz.muni.fi.annotation.LoggerContext;
import cz.muni.fi.slf4j.Slf4jLoggingCallback;
import org.slf4j.LoggerFactory;

public class Example {

    @LoggerContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Default")
            )
    );

    @LoggerContext(context = BlockCacheContext.class)
    private static StructLogger<BlockCacheContext> structLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Structured")
            )
    );

    @LoggerContext(context = AnotherContext.class)
    private static StructLogger<AnotherContext> anotherContextStructLog = new StructLogger<>(
            new Slf4jLoggingCallback(
                    LoggerFactory.getLogger("cz.muni.fi.Another")
            )
    );

    public static void main(String[] args) {

        int numCached = 0;
        int neededCached = 0;
        long blockId = 0;
        long datanodeUuid = 0;
        String reason = "reason";

        defaultLog.info("Event with double={} and boolean={}")
                .varDouble(1.2)
                .varBoolean(false)
                .log("edu.TestEvent");

        defaultLog.info("Event with double={} and boolean={} and double={} and double={}")
                .varDouble(1.2)
                .varBoolean(true)
                .varDouble(5.6)
                .varDouble(1.0)
                .log();

        structLog.warn("Block removal for dataNode from PENDING_UNCACHED - it was uncached by the dataNode")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .log();

        structLog.warn("Block removal for dataNode from PENDING_UNCACHED - it was uncached by the dataNode")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .log();

        structLog.info("Cannot cache block")
                .blockId(blockId)
                .reason(reason)
                .log("CannotCache");

        structLog.trace("Block removal for dataNode from PENDING_CACHED - we already have enough cached replicas")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .numCached(numCached)
                .neededCached(neededCached)
                .log("cz.muni.BlockRemoval");

        structLog.info("Block removal for dataNode from PENDING_UNCACHED - we do not have enough cached replicas")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .numCached(numCached)
                .neededCached(neededCached)
                .log("cz.BlockRemoval");

        structLog.info("Block removal for dataNode from cachedBlocks - neededCached == 0, and pendingUncached and pendingCached are empty.")
                .blockId(blockId)
                .log();

        structLog.info("Block removal for dataNode from PENDING_UNCACHED - it was uncached by the dataNode")
                .blockId(new Object().hashCode())
                .dataNodeUuid(someMethod())
                .log();

        structLog.error("Event with blockId")
                .blockId(blockId)
                .log();

        anotherContextStructLog.info("Event with context")
                .context("ahoj")
                .log();

        structLog.info("Block removal for dataNode from PENDING_UNCACHED - it was uncached by the dataNode")
                .blockId(new Object().hashCode())
                .dataNodeUuid(someMethod())
                .log();

        structLog.info("Event with object")
                .object(new Test("ahoj"))
                .log();

        structLog.audit("Audit event with blockId and dataNodeUuid")
                .blockId(1)
                .dataNodeUuid(2)
                .log();
    }

    private static int someMethod() {
        return 0;
    }
}