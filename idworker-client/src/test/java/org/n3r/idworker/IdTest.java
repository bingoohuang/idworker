package org.n3r.idworker;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

public class IdTest {
    @BeforeClass
    public static void beforeClass() {
        String pathname = System.getProperty("user.home") + File.separator + ".idworkers";
        new File(pathname).mkdir();
        String ipdotlock = Ip.ip + ".lock.0112";
        try {
            new File(pathname, ipdotlock).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void afterClass() {
        String pathname = System.getProperty("user.home") + File.separator + ".idworkers";
        String ipdotlock = Ip.ip + ".lock.0112";
        new File(pathname, ipdotlock).delete();
        new File(pathname).delete();
    }

    @Test
    public void test() {
        BigInteger bigInteger = new BigInteger("111111111100000000000", 2);
        long workerMask = bigInteger.longValue();

        long id = Id.next();
        assertTrue(((workerMask & id) >> 11) == 112L);
    }
}
