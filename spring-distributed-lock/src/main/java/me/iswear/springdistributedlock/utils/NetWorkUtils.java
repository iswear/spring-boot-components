package me.iswear.springdistributedlock.utils;

import org.apache.commons.codec.binary.Hex;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by iswear on 2017/10/22.
 */
public class NetWorkUtils {

    /**
     * 获取第一块网卡物理地址
     * @return
     * @throws SocketException
     */
    public static String getFirstAvailableHardwareAddressHexString() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.getHardwareAddress() != null && networkInterface.getHardwareAddress().length > 0) {
                return Hex.encodeHexString(networkInterface.getHardwareAddress());
            }
        }
        throw new NullPointerException();
    }

}
