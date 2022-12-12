package com.xiao.nettydemo.protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class SequenceIdGenerator {
    private static final AtomicInteger id = new AtomicInteger();

    public static int nexId(){
        return id.incrementAndGet();
    }
}
