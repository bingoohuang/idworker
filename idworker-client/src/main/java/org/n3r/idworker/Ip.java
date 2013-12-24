package org.n3r.idworker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Ip {
    static Logger logger = LoggerFactory.getLogger(Ip.class);
    public static String ip;

    static {
        try {
            InetAddress localHostLANAddress = getFirstNonLoopbackAddress();
            ip = localHostLANAddress.getHostAddress();
        } catch (Exception e) {
            logger.error("get ipv4 failed ", e);
        }
    }

    private static InetAddress getFirstNonLoopbackAddress() throws SocketException {
        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (addr.isLoopbackAddress()) continue;

                if (addr instanceof Inet4Address) {
                    return addr;
                }
            }
        }
        return null;
    }

}
