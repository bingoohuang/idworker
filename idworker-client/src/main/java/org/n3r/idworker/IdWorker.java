package org.n3r.idworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class IdWorker {
    protected long epoch = 1387886498127L; // 2013-12-24 20:01:38.127

    protected long workerIdBits = 10L;
    protected long maxWorkerId = -1L ^ (-1L << workerIdBits);
    protected long sequenceBits = 11L;

    protected long workerIdShift = sequenceBits;
    protected long timestampLeftShift = sequenceBits + workerIdBits;
    protected long sequenceMask = -1L ^ (-1L << sequenceBits);

    protected long lastTimestamp = -1L;

    protected long workerId;
    protected long sequence = 0L;
    protected Logger logger = LoggerFactory.getLogger(IdWorker.class);

    public IdWorker(long workerId) {
        this.workerId = checkWorkerId(workerId);

        logger.info("worker starting. timestamp left shift {}, worker id bits {}, sequence bits {}, workerid {}",
                timestampLeftShift, workerIdBits, sequenceBits, workerId);
    }

    public long getEpoch() {
        return epoch;
    }

    private long checkWorkerId(long workerId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            int rand = new SecureRandom().nextInt((int) maxWorkerId + 1);
            logger.warn("worker Id can't be greater than {} or less than 0, use a random {}", maxWorkerId, rand);
            return rand;
        }

        return workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            logger.error("clock is moving backwards.  Rejecting requests until {}.", lastTimestamp);
            throw new InvalidSystemClock(String.format(
                    "Clock moved backwards.  Refusing to generate id for {} milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) timestamp = tilNextMillis(lastTimestamp);
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        return ((timestamp - epoch) << timestampLeftShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
