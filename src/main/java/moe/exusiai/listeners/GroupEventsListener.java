package moe.exusiai.listeners;

import moe.exusiai.data.MessageFilterData;
import moe.exusiai.handler.messagefilter.MessageFilter;
import moe.exusiai.network.NetworkMojang;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.MessageSource;

public class GroupEventsListener extends SimpleListenerHost {
//    public HashMap<Long, Integer> muteInTenMinutes = new HashMap<Long, Integer>();
//    public HashMap<Long, Integer> muteTime = new HashMap<Long, Integer>();

    @EventHandler
    public void onMessage(GroupMessageEvent event) {
        Member sender = event.getSender();
        String message = event.getMessage().contentToString();
        MessageFilter messageFilter = new MessageFilter();
        Boolean isSensitive = messageFilter.MessageFilter(message);
        if (isSensitive) {
            MessageSource.recall(event.getMessage().get(MessageSource.Key));
            if (MessageFilterData.recallInTenMinutes.get(String.valueOf(sender.getId())) != null) {
                Integer memberRecallTimes = MessageFilterData.recallInTenMinutes.get(String.valueOf(sender.getId()));
                if (memberRecallTimes < 3) {
                    memberRecallTimes++;
                    MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), memberRecallTimes);
                    return;
                } else {
                    if (MessageFilterData.muteTime.get(String.valueOf(sender.getId())) == null) {
                        sender.mute(60);
                        MessageFilterData.muteTime.put(String.valueOf(sender.getId()), 60);
                        MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), 0);
                        return;
                    } else {
                        Integer memberMuteTime = MessageFilterData.muteTime.get(String.valueOf(sender.getId())) + 120;
                        MessageFilterData.muteTime.put(String.valueOf(sender.getId()), memberMuteTime);
                        sender.mute(memberMuteTime);
                        MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), 0);
                        return;
                    }
                }
            } else {
                MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), 1);
            }

        }
        return;
    }

    @EventHandler
    public void onMemberJoinRequest(MemberJoinRequestEvent event) {
        String username = event.getMessage().split("：")[2];
        Boolean userExist = NetworkMojang.MojangUserNameExist(username);
        if (userExist) {
            event.accept();
            Long requesterId = event.getFromId();
            event.getGroup().getMembers().get(requesterId).sendMessage("欢迎入群");
        } else {
            event.reject(false, "该玩家不存在");
        }
    }
}
