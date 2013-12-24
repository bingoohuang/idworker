package org.n3r.idworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkerIdServerLock {
    static File dir = new File(System.getProperty("user.home") + File.separator + ".idworkers");
    static FileServerLock globalWorkerIdFileServerLock;
    static volatile long currentIndex = -1L;
    static Logger logger = LoggerFactory.getLogger(WorkerIdServerLock.class);

    static {
        dir.mkdirs();
        if (!dir.exists()) throw new RuntimeException("create id workers dir fail " + dir);

        try {
            File file = new File(dir, "workerid.index");
            file.createNewFile();

            FileServerLock fileServerLock = new FileServerLock(file);
            if (!fileServerLock.tryLock()) {
                logger.error("try lock wokerid.index failed");
                throw new RuntimeException("try lock wokerid.index failed");
            }

            globalWorkerIdFileServerLock = fileServerLock;
            currentIndex = globalWorkerIdFileServerLock.getIndex();
        } catch (Exception e) {
            logger.error("initialize error", e);
            throw new RuntimeException(e);
        }

    }

    public static long current() {
        return currentIndex;
    }

    static long workerIdBits = 10L;
    static long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private static synchronized long increment() {
        long nextIndex = currentIndex + 1;
        if (nextIndex == maxWorkerId) nextIndex = 0;

        globalWorkerIdFileServerLock.setIndex(nextIndex);
        currentIndex = nextIndex;
        return currentIndex;
    }

    public static synchronized String increment(String ip) {
        long newWorkerId = increment();
        String str = String.format("%04d", newWorkerId);
        synchronized (ip) {
            try {
                new File(dir, ip + ".lock." + str).createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return str;
    }

    static Pattern lockFilePattern = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}).lock.(\\d{4})");

    public static String list() {
        StringBuilder ret = new StringBuilder();
        Set<String> ips = new HashSet<String>();

        for (File file : dir.listFiles()) {
            Matcher matcher = lockFilePattern.matcher(file.getName());
            if (!matcher.matches()) continue;

            String ip = matcher.group(1);
            if (ips.contains(ip)) continue;

            if (ret.length() > 0) ret.append(';');
            ret.append(ip).append(":");
            ips.add(ip);

            boolean first = true;
            for (File f : dir.listFiles()) {
                Matcher matcher2 = lockFilePattern.matcher(f.getName());
                if (!matcher2.matches()) continue;
                if (!matcher2.group(1).equals(ip)) continue;

                if (!first) ret.append(',');
                else first = false;
                ret.append(matcher2.group(2));
            }
        }

        return ret.toString();
    }

    public static String list(String ip) {
        StringBuilder sb = new StringBuilder();
        String prefix = ip + ".lock.";
        int prefixSize = prefix.length();
        for (File file : dir.listFiles()) {
            // check the format like 10.142.1.151.lock.0001
            if (!file.getName().startsWith(prefix)) continue;

            String workerId = file.getName().substring(prefixSize);
            if (!workerId.matches("\\d\\d\\d\\d")) continue;

            if (sb.length() > 0) sb.append(',');
            sb.append(workerId);
        }

        return sb.toString();
    }

}
