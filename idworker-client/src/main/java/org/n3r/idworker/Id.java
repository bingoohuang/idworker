package org.n3r.idworker;

import org.n3r.idworker.strategy.DefaultWorkerIdStrategy;

public class Id {
    private static WorkerIdStatrategy workerIdStrategy = DefaultWorkerIdStrategy.instance;
    private static IdWorker idWorker = new IdWorker(workerIdStrategy.availableWorkerId());

    public static void configure(WorkerIdStatrategy custom) {
        workerIdStrategy = custom;
        idWorker = new IdWorker(workerIdStrategy.availableWorkerId());
    }

    public static long next() {
        return idWorker.nextId();
    }


}
