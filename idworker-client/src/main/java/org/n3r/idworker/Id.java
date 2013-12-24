package org.n3r.idworker;

public class Id {
    private static IdWorker idWorker = new IdWorker(WorkerIdLock.workerId);

    public static long next() {
        return idWorker.nextId();
    }
}
