package com.foogaro.data.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class RedisCache {

    public static void main(String[] args) {
        InetAddress[] ips;
        try {
            ips = InetAddress.getAllByName("redis-12000.redis.foogaro.cloud");
            for (InetAddress ip : ips) {
                System.out.println("Your current IP address : " + ip.getHostAddress());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}
