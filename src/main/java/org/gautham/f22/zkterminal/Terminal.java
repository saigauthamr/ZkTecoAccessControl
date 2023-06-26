package org.gautham.f22.zkterminal;

import org.apache.commons.lang3.StringUtils;
import org.gautham.f22.events.EventCode;
import org.gautham.f22.utils.HexUtils;
import org.gautham.f22.utils.SecurityUtils;
import org.gautham.f22.zkCommands.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Date;

public class Terminal {

    private final String ip;
    private final int port;
    private DatagramSocket socket;
    private InetAddress address;
    private int sessionId;
    private int replyNo;

    public Terminal(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public CommandReply connect() throws IOException {
        sessionId = 0;
        replyNo = 0;

        socket = new DatagramSocket(port);
        address = InetAddress.getByName(ip);

        int[] toSend = Command.getPacket(CommandCode.CMD_CONNECT, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        sessionId = response[4] + (response[5] * 0x100);
        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public CommandReply enableDevice() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_ENABLEDEVICE, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public CommandReply disableDevice() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_DISABLEDEVICE, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public CommandReply connectAuth(int comKey) throws IOException {
        int[] key = SecurityUtils.authKey(comKey, sessionId);

        int[] toSend = Command.getPacket(CommandCode.CMD_AUTH, sessionId, replyNo, key);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public CommandReply enableRealtime(EventCode... events) throws IOException {
        int allEvents = 0;

        for (EventCode event : events) {
            allEvents = allEvents | event.getCode();
        }

        String hex = StringUtils.leftPad(Integer.toHexString(allEvents), 8, "0");

        int[] eventReg = new int[4];
        int index = 3;

        while (hex.length() > 0) {
            eventReg[index] = (int) Long.parseLong(hex.substring(0, 2), 16);
            index--;

            hex = hex.substring(2);
        }

        System.out.println(HexUtils.bytesToHex(eventReg));

        int[] toSend = Command.getPacket(CommandCode.CMD_REG_EVENT, sessionId, replyNo, eventReg);
        byte[] buf = new byte[toSend.length];

        index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public CommandReply getAttendanceRecords() throws IOException, ParseException {
        int[] toSend = Command.getPacket(CommandCode.CMD_ATTLOG_RRQ, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        StringBuilder attendanceBuffer = new StringBuilder();

        if (replyCode == CommandReplyCode.CMD_PREPARE_DATA) {
            boolean first = true;

            int lastDataRead;

            do {
                int[] readData = readResponse();

                lastDataRead = readData.length;

                String readPacket = HexUtils.bytesToHex(readData);

                attendanceBuffer.append(readPacket.substring(first ? 24 : 16));

                first = false;
            } while (lastDataRead == 1032);
        } else {
            attendanceBuffer.append(HexUtils.bytesToHex(response).substring(24));
        }

        String attendance = attendanceBuffer.toString();

        while (attendance.length() > 0) {
            String record = attendance.substring(0, 80);

            int seq = Integer.valueOf(record.substring(2, 4) + record.substring(0, 2), 16);

            record = record.substring(4);

            String userId = Character.toString((char) Integer.valueOf(record.substring(0, 2), 16).intValue()) + (char) Integer.valueOf(record.substring(2, 4), 16).intValue() + (char) Integer.valueOf(record.substring(4, 6), 16).intValue() + (char) Integer.valueOf(record.substring(6, 8), 16).intValue() + (char) Integer.valueOf(record.substring(8, 10), 16).intValue() + (char) Integer.valueOf(record.substring(10, 12), 16).intValue() + (char) Integer.valueOf(record.substring(12, 14), 16).intValue() + (char) Integer.valueOf(record.substring(14, 16), 16).intValue() + (char) Integer.valueOf(record.substring(16, 18), 16).intValue();

            System.out.println(userId);

            record = record.substring(48);

            int method = Integer.valueOf(record.substring(0, 2), 16);

            record = record.substring(2);

            long encDate = Integer.valueOf(record.substring(6, 8), 16) * 0x1000000L + (Integer.valueOf(record.substring(4, 6), 16) * 0x10000L) + (Integer.valueOf(record.substring(2, 4), 16) * 0x100L) + (Integer.valueOf(record.substring(0, 2), 16));

            Date attendanceDate = HexUtils.extractDate(encDate);

            System.out.println(attendanceDate);

            record = record.substring(8);

            int operation = Integer.valueOf(record.substring(0, 2), 16);

            attendance = attendance.substring(80);
        }

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, sessionId, replyId, payloads);
    }

    public GetTimeReply getDeviceTime() throws IOException, ParseException {
        int[] toSend = Command.getPacket(CommandCode.CMD_GET_TIME, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new GetTimeReply(replyCode, sessionId, replyId, payloads);
    }

    public void disconnect() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_EXIT, sessionId, replyNo, null);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        socket.close();
    }

    public int[] readResponse() throws IOException {
        byte[] buf = new byte[1000000];

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

        int[] response = new int[packet.getLength()];

        for (int i = 0; i < response.length; i++) {
            response[i] = buf[i] & 0xFF;
        }

        return response;

        /*int index = 0;
        int[] data = new int[1000000];

        int read;
        int size = 0;

        boolean reading = true;

        while (reading && (read = is.read()) != -1) {
            if (index >= 4 && index <= 7) {
                size += read * Math.pow(16, index - 4);
            } else if (index > 7) {
                if (index - 7 >= size) {
                    reading = false;
                }
            }

            data[index] = read;
            index++;
        }

        int[] finalData = new int[index];

        System.arraycopy(data, 0, finalData, 0, index);

        return finalData;*/
    }

    public void doorUnlock(int delay) throws IOException {
        int[] payload = {delay};

        int[] toSend = Command.getPacket(CommandCode.CMD_UNLOCK, sessionId, replyNo, payload);
        byte[] buf = new byte[toSend.length];

        int index = 0;

        for (int byteToSend : toSend) {
            buf[index++] = (byte) byteToSend;
        }

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        replyNo++;

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        CommandReply commandReply = new CommandReply(replyCode, sessionId, replyId, payloads);
    }


}