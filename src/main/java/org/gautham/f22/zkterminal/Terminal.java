package org.gautham.f22.zkterminal;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.gautham.f22.DTO.ConvDTO;
import org.gautham.f22.events.EventCode;
import org.gautham.f22.utils.HexUtils;
import org.gautham.f22.utils.SecurityUtils;
import org.gautham.f22.zkcommands.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import static org.gautham.f22.utils.ConnectionHandler.*;
import static org.gautham.f22.utils.PacketUtils.*;

public class Terminal {

    private static final Logger logger = LoggerFactory.getLogger(Terminal.class);
    private ConvDTO convDTO;

    public Terminal() {
        convDTO = new ConvDTO();
    }

    public void setConvDTO(ConvDTO convDTO) {
        this.convDTO = convDTO;
    }

    public CommandReply connect() throws IOException {
        convDTO.setSessionId(0);
        convDTO.setReplyNo(0);

        int[] toSend = Command.getPacket(CommandCode.CMD_CONNECT, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        convDTO.setSessionId(response[4] + (response[5] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
    }


    public CommandReply enableDevice() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_ENABLEDEVICE, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
    }

    public CommandReply disableDevice() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_DISABLEDEVICE, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
    }

    public CommandReply connectAuth(int comKey) throws IOException {
        int[] key = SecurityUtils.authKey(comKey, convDTO.getSessionId());

        int[] toSend = Command.getPacket(CommandCode.CMD_AUTH, convDTO.getSessionId(), convDTO.getReplyNo(), key);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);
        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
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

        int[] toSend = Command.getPacket(CommandCode.CMD_REG_EVENT, convDTO.getSessionId(), convDTO.getReplyNo(), eventReg);
        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
    }

    public CommandReply getAttendanceRecords() throws IOException, ParseException {
        int[] toSend = Command.getPacket(CommandCode.CMD_ATTLOG_RRQ, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);
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

        return new CommandReply(replyCode, convDTO, convDTO.getSessionId(), replyId, payloads);
    }

    public Date getDeviceTime() throws IOException, ParseException {
        int[] toSend = Command.getPacket(CommandCode.CMD_GET_TIME, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        convDTO.setReplyNo(convDTO.getReplyNo() + 1);

        int[] response = readResponse();

        CommandReplyCode replyCode = CommandReplyCode.decode(response[0] + (response[1] * 0x100));

        int replyId = response[6] + (response[7] * 0x100);

        int[] payloads = new int[response.length - 8];

        System.arraycopy(response, 8, payloads, 0, payloads.length);

        GetTimeReply getTimeReply = new GetTimeReply(replyCode, convDTO.getSessionId(), replyId, payloads, convDTO);
        return getTimeReply.getDeviceDate();
    }

    public void disconnect() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_EXIT, convDTO.getSessionId(), convDTO.getReplyNo(), null);

        byte[] buf = convertIntToByteArray(toSend);

        sendPacket(buf, buf.length, address, port);

        socket.close();
    }
}