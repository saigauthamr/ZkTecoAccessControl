package org.gautham.f22;

import org.gautham.f22.zkCommands.CommandReply;
import org.gautham.f22.zkCommands.GetTimeReply;
import org.gautham.f22.zkterminal.Terminal;

import java.io.IOException;
import java.text.ParseException;
import java.util.logging.Logger;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, ParseException {
        Terminal terminal = new Terminal("192.168.29.15", 4370);
        CommandReply reply = terminal.connect();
        logger.info(String.valueOf(reply.getCode()));
        reply = terminal.connectAuth(0);
        logger.info(String.valueOf(reply.getCode()));
        GetTimeReply getTimeReply = terminal.getDeviceTime();
        logger.info(getTimeReply.toString());
        reply = terminal.disableDevice();
        logger.info(String.valueOf(reply.getCode()));
        terminal.doorUnlock(5);
        logger.info("Door Unlocked");
        reply = terminal.enableDevice();
        logger.info(String.valueOf(reply.getCode()));
        terminal.disconnect();
        logger.info(String.valueOf(reply.getCode()));
    }
}
