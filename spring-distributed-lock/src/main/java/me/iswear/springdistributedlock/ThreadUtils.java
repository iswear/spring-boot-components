package me.iswear.springdistributedlock;

import java.lang.management.ManagementFactory;

public class ThreadUtils {

    public static boolean isThreadAlive(long threadId) {
        return ManagementFactory.getThreadMXBean().getThreadInfo(threadId) != null;
    }

}
