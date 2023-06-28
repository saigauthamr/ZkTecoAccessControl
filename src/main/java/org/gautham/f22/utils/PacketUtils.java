package org.gautham.f22.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import static org.gautham.f22.utils.ConnectionHandler.socket;

public class PacketUtils {
    public static int[] readResponse() throws IOException {

        byte[] buf = new byte[1000000];

        DatagramPacket packet = receivePacket(buf, buf.length);

        int[] response = convertByteArrayToIntArray(packet.getData());

        return response;
    }

    public static byte[] convertIntToByteArray(int[] data) {
        byte[] byteArray = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            byteArray[i] = (byte) data[i];
        }

        return byteArray;
    }

    public static int[] convertByteArrayToIntArray(byte[] byteArray) {
        int[] intArray = new int[byteArray.length];

        for (int i = 0; i < byteArray.length; i++) {
            intArray[i] = byteArray[i] & 0xFF;
        }

        return intArray;
    }

    public static void sendPacket(byte[] data, int length, InetAddress address, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    public static DatagramPacket receivePacket(byte[] data, int length) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);
        return packet;
    }
}
