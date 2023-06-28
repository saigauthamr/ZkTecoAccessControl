package org.gautham.f22.utils;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectionHandler {
    public static int port;
    public static String ip;
    public static DatagramSocket socket;
    public static InetAddress address;
    private final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);

    public ConnectionHandler(String ip, int port) {
        ConnectionHandler.ip = ip;
        ConnectionHandler.port = port;
        logger.warn("ConnectionHandler Initialized");
        ConnectionInit(ip, port);
    }

    public static void ConnectionInit(String ip, int port) {
        try {
            socket = new DatagramSocket(port);
            address = InetAddress.getByName(ip);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
