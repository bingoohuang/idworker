package org.n3r.idworker;

import org.junit.Test;
import org.n3r.idworker.utils.IPv4;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasedIpWorkerIdTest {
    public static ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null ? contextClassLoader : BasedIpWorkerIdTest.class.getClassLoader();
    }


    public static InputStream classResourceToStream(String resourceName) {
        return getClassLoader().getResourceAsStream(resourceName);
    }

    @Test
    public void test1() throws IOException {
        InputStream inputStream = classResourceToStream("ess_tail_out.conf");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String line;

        Set<String> ips = new HashSet<String>();
        Set<Long> availableIds = new HashSet<Long>();
        while ((line = bufferedReader.readLine()) != null) {
            String ip = findIp(line);
            if (ip == null) continue;
            if (ips.contains(ip)) continue;
            ips.add(ip);

            computeWorkerIds(availableIds, ip);
        }
    }

    static long workerIdBits = 10L;
    static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private void computeWorkerIds(Set<Long> availableIds, String ip) {
        long lip = IPv4.toLong(ip);
        System.out.print(ip);
        System.out.print(',');
        System.out.print(lip);
        System.out.print(',');
        long oid = lip & maxWorkerId;
        long id = oid;
        checkDuplicated(availableIds, id);
        id = id ^ 345L;
        checkDuplicated(availableIds, id);
        id = id ^ 923L;
        checkDuplicated(availableIds, id);
        id = id ^ 832L;
        checkDuplicated(availableIds, id);
//        id = oid ^ 63L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 127L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 255L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 511L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 341L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 1023L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 93L;
//        checkDuplicated(availableIds, id);
//        id = oid ^ 345L;
//        checkDuplicated(availableIds, id);
        System.out.println();
    }

    private void checkDuplicated(Set<Long> availableIds, long id) {
        if (availableIds.contains(id)) {
            System.out.print("duplicated:" + id + ",");
        } else {
            availableIds.add(id);
            System.out.print(id);
            System.out.print(',');
        }

    }

    static Pattern ipv4Pattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    public static String findIp(String line) {
        Matcher matcher = ipv4Pattern.matcher(line);
        if (matcher.find()) return matcher.group();
        return null;
    }


}
