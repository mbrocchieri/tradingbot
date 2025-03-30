package org.tradingbot.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class StrategyLock {
    public final static StrategyLock INSTANCE = new StrategyLock();
    private final static Map<Integer, ReentrantLock> locksMap = new ConcurrentHashMap<>();

    private StrategyLock() {

    }

    public void lock(int id) {
        locksMap.compute(id, (key, lock) -> {
            if (lock == null) {
                return new ReentrantLock();
            }
            return lock;
        }).lock();

    }

    public void unlock(int id) {
        locksMap.get(id).unlock();
    }

}
