package org.gautham.f22.zkaccess;


import org.gautham.f22.DTO.ConvDTO;
import org.gautham.f22.zkcommands.Command;
import org.gautham.f22.zkcommands.CommandCode;
import org.gautham.f22.zkcommands.CommandReply;
import org.gautham.f22.zkcommands.CommandReplyCode;

import java.io.IOException;

import static org.gautham.f22.utils.ConnectionHandler.address;
import static org.gautham.f22.utils.ConnectionHandler.port;
import static org.gautham.f22.utils.PacketUtils.*;

public class Access {
    private ConvDTO convDTO = new ConvDTO();

    public void setConvDTO(ConvDTO convDTO) {
        this.convDTO = convDTO;
    }


//    public CommaNdReply getGroupInfo(int groupNo) throws IOException {
//        byte[] grpReq = new byte[8];
//        writeIntToByteArray(grpReq, groupNo, ByteOrder.LITTLE_ENDIAN);
//
//        byte[] requestData = grpReq;
//        sendGroupInfoRequest(requestData);
//
//        byte[] responseData = receiveGroupInfoReply();
//
//        List<Integer> groupTzs = extractGroupTimezones(responseData);
//        int groupVerifyStyle = extractGroupVerifyStyle(responseData);
//        int groupHolidaysFlg = extractGroupHolidaysFlag(responseData);
//
//        return new CommandReply(CommandReplyCode.SUCCESS, convDTO, convDTO.getSessionId(), convDTO.getReplyNo(), new int[]{groupNo, groupTzs.size(), groupVerifyStyle, groupHolidaysFlg});
//    }


    public CommandReply doorUnlock(int delay) throws IOException {
        int[] payload = {delay};

        int[] toSend = Command.getPacket(CommandCode.CMD_UNLOCK, convDTO.getSessionId(), convDTO.getReplyNo(), payload);

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

    public CommandReply getDoorState() throws IOException {
        int[] toSend = Command.getPacket(CommandCode.CMD_DOORSTATE_RRQ, convDTO.getSessionId(), convDTO.getReplyNo(), null);

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

}
