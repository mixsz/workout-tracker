package com.mixsz.workouttracker.infra.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = 60_000;

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public int remainingAttempts(String ip) {
        Bucket bucket = buckets.computeIfAbsent(ip, k -> new Bucket());
        long now = System.currentTimeMillis();

        synchronized (bucket) {
            if (now - bucket.windowStart > WINDOW_MILLIS) {
                bucket.windowStart = now;
                bucket.attempts.set(0);
            }
            int current = bucket.attempts.incrementAndGet();
            return MAX_ATTEMPTS - current;
        }
    }

    private static class Bucket {
        long windowStart = System.currentTimeMillis();
        AtomicInteger attempts = new AtomicInteger(0);
    }
}