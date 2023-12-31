package org.gautham.f22.zkcommands;

import org.gautham.f22.DTO.ConvDTO;

public class CommandReply {
    private final CommandReplyCode code;
    private final ConvDTO convDTO;
    private final int sessionId;
    private final int replyId;
    private final int[] payloads;

    public CommandReply(CommandReplyCode code, ConvDTO convDTO, int sessionId, int replyId, int[] payloads) {
        this.code = code;
        this.convDTO = convDTO;
        this.sessionId = sessionId;
        this.replyId = replyId;
        this.payloads = payloads;
    }

    /**
     * @return the code
     */
    public CommandReplyCode getCode() {
        return code;
    }

    public int getSessionId() {
        return sessionId;
    }

    public ConvDTO getConvDTO() {
        return convDTO;
    }

    /**
     * @return the replyId
     */
    public int getReplyId() {
        return replyId;
    }

    /**
     * @return the payloads
     */
    public int[] getPayloads() {
        return payloads;
    }


}