package cz.muni.fi;

import cz.muni.fi.annotation.VarContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Example {

    @VarContext(context = DefaultContext.class)
    private static StructLogger<DefaultContext> defaultLog = StructLogger.instance();

    @VarContext(context = BlockCacheContext.class)
    private static StructLogger<BlockCacheContext> structLog = StructLogger.instance();

    @VarContext(context = AnotherContext.class)
    private static StructLogger<AnotherContext> anotherContextStructLog = StructLogger.instance();

    static Logger logger = LoggerFactory.getLogger("Example");

    private Map<String,Integer> map = new HashMap() {{
        put("ahoj", 1);
    }};

    public static void main(String[] args) throws Exception {

        new Predicate<Integer>(){
            @VarContext(context = DefaultContext.class)
            private StructLogger<AnotherContext> structlog2 = StructLogger.instance();


            @Override
            public boolean test(final Integer o) {
                structlog2.error("errorek").log();
                return false;
            }
        };

        String message = MessageFormatter.arrayFormat("ahoj {}", Arrays.asList(1).toArray()).getMessage();
        System.out.println(message);
        logger.error("ahoj {}", new Test("aa").toString());

        int numCached = 0;
        int neededCached = 0;
        long blockId = 0;
        long datanodeUuid = 0;
        String reason = "reason";

        long startTime = System.currentTimeMillis();

        defaultLog.info("test {} string literal {}")
                .varDouble(1.2)
                .varBoolean(false)
                .log();

        defaultLog.info("cau3 {} {} {} mnau {} {} {}")
                .varDouble(1.2)
                .varBoolean(true)
                .varDouble(5.6)
                .varDouble(1.0)
                .varBoolean(false)
                .varBoolean(false)
                .log();

        structLog.warn("Block removal {} for dataNode from {} PENDING_UNCACHED - it was uncached by the dataNode.")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .log();

        structLog.warn("Block removal {} for dataNode from {} PENDING_UNCACHED - it was uncached by the dataNode.")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .log();

        structLog.warn("Block removal {} for dataNode from {} PENDING_UNCACHED - it was uncached by the dataNode.")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .log();

        structLog.info("Cannot cache block {} because {}")
                .blockId(blockId)
                .reason(reason)
                .log();

        structLog.info("Block {} removal for dataNode {} from PENDING_CACHED - we already have enough cached replicas {} {}")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .numCached(numCached)
                .neededCached(neededCached)
                .log();

        structLog.info("Block {} removal for dataNode {} from PENDING_UNCACHED - we do not have enough cached replicas {} {}")
                .blockId(blockId)
                .dataNodeUuid(datanodeUuid)
                .numCached(numCached)
                .neededCached(neededCached)
                .log();

        structLog.info("Block {} removal for dataNode from cachedBlocks - neededCached == 0, and pendingUncached and pendingCached are empty.")
                .blockId(blockId)
                .log();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);


        structLog.info("Block {} removal for dataNode {} from PENDING_UNCACHED - it was uncached by the dataNode.")
                .blockId(new Object().hashCode())
                .dataNodeUuid(in())
                .log();

        structLog.error("errorek {}")
                .blockId(blockId)
                .log();

        anotherContextStructLog.info("ahoj {} ")
                .context("ahoj")
                .log();

        structLog.info("Block {} removal for dataNode {} from PENDING_UNCACHED - it was uncached by the dataNode.")
                .blockId(new Object().hashCode())
                .dataNodeUuid(in())
                .log();

        structLog.info("ahojkya {}")
                .object(new Test("ahoj"))
                .log();

        new Example().new Petr().doIt();
    }

    private static void a(Object o) {
        System.out.println(o);
    }

    private static int in() {
        return 0;
    }

    class Petr {
        @VarContext(context = AnotherContext.class)
        private StructLogger<AnotherContext> anotherContextStructLog = StructLogger.instance();

        private int b;

        public void doIt() {
            anotherContextStructLog.error("tu").log();

        }
    }
}