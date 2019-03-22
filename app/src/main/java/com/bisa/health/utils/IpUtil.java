package com.bisa.health.utils;

import com.lib.FunSDK;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {
    public static String formatIpAddress(int ip ) {
        byte[] ipAddress = new byte[4];
        InetAddress myaddr;
        try {
            ipAddress[3] = (byte)((ip >> 24) & 0xff);
            ipAddress[2] = (byte)((ip >> 16) & 0xff);
            ipAddress[1] = (byte)((ip >> 8) & 0xff);
            ipAddress[0] = (byte)(ip & 0xff);
            myaddr = InetAddress.getByAddress(ipAddress);
            String hostaddr = myaddr.getHostAddress();
            return hostaddr;
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

}
