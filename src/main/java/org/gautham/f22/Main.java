package org.gautham.f22;

import org.gautham.f22.utils.ConnectionHandler;
import org.gautham.f22.zkaccess.Access;
import org.gautham.f22.zkcommands.CommandReply;
import org.gautham.f22.zkterminal.Terminal;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, ParseException {

        // Create a Terminal object and establish a connection with the specified IP address and port
        String ipAddress = "192.168.29.15";
        int port = 4370;
        ConnectionHandler connectionHandler = new ConnectionHandler(ipAddress, port);
        Terminal terminal = new Terminal();
        Access access = new Access();

        // Connect to the terminal device and obtain a command reply
        logger.info("connect");
        CommandReply reply = terminal.connect();
        logger.info(String.valueOf(reply.getCode())); // Log the reply code

        // Authenticate the connection with the specified authentication code
        logger.info("connectAuth");
        reply = terminal.connectAuth(580);
        logger.info(String.valueOf(reply.getCode())); // Log the reply code

        // Get the current device time from the terminal
        logger.info("getDeviceTime");
        Date date = terminal.getDeviceTime();
        logger.info(String.valueOf(date)); // Log the device time

        // Disable the terminal device
        logger.info("disableDevice");
        reply = terminal.disableDevice();
        logger.info(String.valueOf(reply.getCode())); // Log the reply code
        // Get Attendance Records
        //logger.info("getAttendanceRecords");
        //reply = terminal.getAttendanceRecords();
        //logger.info(Arrays.toString(reply.getPayloads())); // Log the reply code
        // Unlock the door associated with delay 5 seconds
        access.setConvDTO(reply.getConvDTO());
        logger.info("doorUnlock");
        access.doorUnlock(5);
        //Get the door state
        logger.info("getDoorState");
        reply = access.getDoorState();
        logger.info(Arrays.toString(new int[]{reply.getPayloads()[0]})); // Log the reply code

        // Enable the terminal device
        logger.info("enableDevice");
        terminal.setConvDTO(reply.getConvDTO());
        reply = terminal.enableDevice();
        logger.info(String.valueOf(reply.getCode())); // Log the reply code

        // Disconnect from the terminal device
        logger.info("disconnect");
        terminal.disconnect();
        logger.info(String.valueOf(reply.getCode())); // Log the reply code

    }
}
