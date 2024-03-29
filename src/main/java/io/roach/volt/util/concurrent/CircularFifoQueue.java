package io.roach.volt.util.concurrent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class CircularFifoQueue<K, V> implements FifoQueue<K, V> {
    private final Map<String, BlockingQueue<Map<K, V>>> blockingQueues
            = new ConcurrentHashMap<>();

    private final Map<String, RingBuffer<Map<K, V>>> ringBuffers
            = new ConcurrentHashMap<>();

    private final int bufferCapacity;

    public CircularFifoQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        this.bufferCapacity = capacity;
    }

    private BlockingQueue<Map<K, V>> getOrCreateQueueForKey(String key) {
        blockingQueues.putIfAbsent(key, new LinkedBlockingDeque<>(bufferCapacity));
        return blockingQueues.get(key);
    }

    private RingBuffer<Map<K, V>> getOrCreateBufferForKey(String key) {
        ringBuffers.putIfAbsent(key, new RingBuffer<>(bufferCapacity));
        return ringBuffers.get(key);
    }

    @Override
    public Map<K, V> take(String key) {
        try {
            return getOrCreateQueueForKey(key).take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<K, V> selectRandom(String key) {
        Map<K, V> values = getOrCreateBufferForKey(key).getRandom();
        if (values == null) {
            values = take(key);
            put(key, values);
        }
        return values;
    }

    @Override
    public void put(String key, Map<K, V> values) {
        try {
            getOrCreateQueueForKey(key).put(values);
            getOrCreateBufferForKey(key).add(values);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void offer(String key, Map<K, V> values) {
        BlockingQueue<Map<K, V>> queueForKey = getOrCreateQueueForKey(key);
        while (!queueForKey.offer(values)) {
            queueForKey.poll();
        }
        getOrCreateBufferForKey(key).add(values);
    }
}
