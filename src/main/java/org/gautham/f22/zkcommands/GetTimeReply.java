package org.gautham.f22.zkcommands;

import org.gautham.f22.DTO.ConvDTO;
import org.gautham.f22.utils.HexUtils;

import java.text.ParseException;
import java.util.Date;

public class GetTimeReply extends CommandReply {

    private final Date deviceDate;

    public GetTimeReply(CommandReplyCode code, int sessionId, int replyId, int[] payloads, ConvDTO convDTO) throws ParseException {
        super(code, convDTO, sessionId, replyId, payloads);

        String payloadsStr = HexUtils.bytesToHex(payloads);

        long encDate = Integer.valueOf(payloadsStr.substring(6, 8), 16) * 0x1000000L + (Integer.valueOf(payloadsStr.substring(4, 6), 16) * 0x10000L) + (Integer.valueOf(payloadsStr.substring(2, 4), 16) * 0x100L) + (Integer.valueOf(payloadsStr.substring(0, 2), 16));

        deviceDate = HexUtils.extractDate(encDate);
    }

    public Date getDeviceDate() {
        return deviceDate;
    }

}