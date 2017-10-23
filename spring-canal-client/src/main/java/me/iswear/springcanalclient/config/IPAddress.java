package me.iswear.springcanalclient.config;

import lombok.Data;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

@Data
public class IPAddress implements Serializable {

    private static final long serialVersionUID = -8718799759289928602L;

    private String host;

    private int ip;

    public SocketAddress toSocketAddress() {
        SocketAddress socketAddress = new InetSocketAddress(this.host, this.ip);
        return socketAddress;
    }

}
