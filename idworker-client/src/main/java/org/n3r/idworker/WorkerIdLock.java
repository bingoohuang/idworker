package org.n3r.idworker;

import java.io.File;

public class WorkerIdLock {
    static File dir = new File(System.getProperty("user.home") + File.separator + ".idworkers");
    static String ipdotlock = Ip.ip + ".lock.";
    static int workerIdIndex = ipdotlock.length();
    static long workerId;
    static FileLock fileLock;

    static {
        dir.mkdirs();
        if (!dir.exists()) throw new RuntimeException("create id workers dir fail " + dir);

        workerId = findAvailWorkerId();
        if (workerId >= 0 ) {
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    fileLock.destroy();
                }
            });
        } else {
            syncWithWorkerIdServer();
        }
    }

    private static void syncWithWorkerIdServer() {

    }


    /**
     * Find the local available worker id.
     *
     * @return -1 when N/A
     */
    private static long findAvailWorkerId() {
        for (File lockFile : dir.listFiles()) {
            // check the format like 10.142.1.151.lock.0001
            if (!lockFile.getName().startsWith(ipdotlock)) continue;

            String workerId = lockFile.getName().substring(workerIdIndex);
            if (!workerId.matches("\\d\\d\\d\\d")) continue;

            FileLock fileLock = new FileLock(lockFile);
            if (!fileLock.tryLock()) {
                fileLock.destroy();
                continue;
            }

            WorkerIdLock.fileLock = fileLock;
            return Long.parseLong(workerId);
        }

        return -1;
    }
}
