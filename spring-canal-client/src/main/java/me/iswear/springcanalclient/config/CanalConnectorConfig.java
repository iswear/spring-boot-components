package me.iswear.springcanalclient.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class CanalConnectorConfig implements Serializable {

    private static final long serialVersionUID = 2251120822446624956L;

    private String destination;

    private String username;

    private String password;

}
