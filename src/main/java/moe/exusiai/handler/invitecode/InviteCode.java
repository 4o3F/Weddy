package moe.exusiai.handler.invitecode;

import moe.exusiai.data.InviteCodeData;

import java.util.HashMap;
import java.util.UUID;

public class InviteCode {

    public static String GenerateInviteCode(String username, Long qqnumber) {
        String invitecode = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        InviteCodeData.invitecode.put(invitecode, username + " " + String.valueOf(qqnumber));
        return invitecode;
    }

    public static HashMap<String, String> CheckInviteCode(String invitecode, Long qqnumber) {
        HashMap<String, String> result = new HashMap<>();
        if (InviteCodeData.invitecode.get(invitecode) == null) {
            result.put("result", "false");
            result.put("username", "null");
            return result;
        } else {
            if (Long.parseLong(InviteCodeData.invitecode.get(invitecode).split(" ")[1]) == qqnumber) {
                result.put("result", "true");
                result.put("username", InviteCodeData.invitecode.get(invitecode).split(" ")[0]);
                InviteCodeData.invitecode.remove(invitecode);
                InviteCodeData.SaveInviteCodeData();
                return result;
            } else {
                result.put("result", "false");
                result.put("username", "null");
                return result;
            }
        }
    }
}
