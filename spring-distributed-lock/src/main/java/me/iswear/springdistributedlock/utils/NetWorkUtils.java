package me.iswear.springdistributedlock.utils;

import org.apache.commons.codec.binary.Hex;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Created by iswear on 2017/10/22.
 */
public class NetWorkUtils {

    public static String getHardwareAddressHexStringByIndex(int index) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByIndex(index);
        if (networkInterface == null) {
            throw new NullPointerException();
        }
        return Hex.encodeHexString(networkInterface.getHardwareAddress());
    }

    public static String getHardwareAddressHexStringByAddress(InetAddress address) throws SocketException {
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(address);
        if (networkInterface == null) {
            throw new NullPointerException();
        }
        return Hex.encodeHexString(networkInterface.getHardwareAddress());
    }

}
