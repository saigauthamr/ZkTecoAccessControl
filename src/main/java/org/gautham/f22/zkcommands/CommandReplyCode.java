package org.gautham.f22.zkcommands;

public enum CommandReplyCode {
    //The request was processed successfully
    CMD_ACK_OK(2000), //There was an error when processing the request
    CMD_ACK_ERROR(2001), CMD_ACK_DATA(2002), CMD_ACK_RETRY(2003), CMD_ACK_REPEAT(2004), //Connection not authorized
    CMD_ACK_UNAUTH(2005), //Received unknown command
    CMD_ACK_UNKNOWN(65535), CMD_ACK_ERROR_CMD(65533), CMD_ACK_ERROR_INIT(65532), CMD_ACK_ERROR_DATA(65531), //Prepare for data transmission
    CMD_PREPARE_DATA(1500), //Data packet
    CMD_DATA(1501);

    private final int code;

    CommandReplyCode(int code) {
        this.code = code;
    }

    public static CommandReplyCode decode(int aCode) {
        for (CommandReplyCode code : values()) {
            if (code.getCode() == aCode) {
                return code;
            }
        }

        throw new RuntimeException("Reply code not recognized");
    }

    public int getCode() {
        return code;
    }
}
