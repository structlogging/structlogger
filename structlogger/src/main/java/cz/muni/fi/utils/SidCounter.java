package cz.muni.fi.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Sequence ID counter
 */
public class SidCounter {
    private static final AtomicLong SEQ_NUMBER = new AtomicLong(); //must be static, generated events use it to generate sequence id

    /**
     * increment and get new value of SID
     */
    public static long incrementAndGet() {
        return SEQ_NUMBER.incrementAndGet();
    }
}
