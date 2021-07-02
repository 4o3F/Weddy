package moe.exusiai.listeners;

import moe.exusiai.data.MessageFilterData;
import moe.exusiai.data.UUIDData;
import moe.exusiai.handler.commandhandler.CommandHandler;
import moe.exusiai.handler.invitecode.InviteCode;
import moe.exusiai.handler.messagefilter.MessageFilter;
import moe.exusiai.handler.specialtitle.SpecialTitle;
import moe.exusiai.network.NetworkMojang;
import moe.exusiai.network.NetworkServer;
import moe.exusiai.utils.ConfigUtil;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GroupEventsListener extends SimpleListenerHost {

    HashMap<Long, String> id = new HashMap<>();
    HashMap<Long, Boolean> bedrock = new HashMap<>();
    public static List<String> minecraftintergration = Arrays.asList(ConfigUtil.config.get("minecraft").get("group").split(","));

    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        Member sender = event.getSender();
        //检测违禁词
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && Boolean.parseBoolean(ConfigUtil.config.get(String.valueOf(event.getGroup().getId())).get("messagefilter"))) {
            String message = event.getMessage().contentToString();
            MessageFilter messageFilter = new MessageFilter();
            Boolean isSensitive = messageFilter.MessageFilter(message);
            if (event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR) || event.getGroup().getBotPermission().equals(MemberPermission.OWNER)) {
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
                            } else {
                                Integer memberMuteTime = MessageFilterData.muteTime.get(String.valueOf(sender.getId())) + 120;
                                MessageFilterData.muteTime.put(String.valueOf(sender.getId()), memberMuteTime);
                                sender.mute(memberMuteTime);
                                MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), 0);
                            }
                            MessageChain messageChain = new MessageChainBuilder()
                                    .append(new At(sender.getId()))
                                    .append(new PlainText("你知道你毫无逻辑的妄言会为罗德岛的舰桥添加什么样的点缀么？"))
                                    .build();
                            event.getGroup().sendMessage(messageChain);
                            return;
                        }
                    } else {
                        MessageFilterData.recallInTenMinutes.put(String.valueOf(sender.getId()), 1);
                    }
                    return;
                }
            } else {
                if (isSensitive) {
                    MessageChain messageChain = new MessageChainBuilder()
                            .append(new PlainText("管理员立即向我报道"))
                            .build();
                    event.getGroup().sendMessage(messageChain);
                    return;
                }
            }
        }

        //转发消息到服务器
        //暂时关闭，等待我重写Lode.Api
//        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && minecraftintergration.contains(String.valueOf(event.getGroup().getId()))) {
//            String message = event.getMessage().contentToString();
//            NetworkServer.ServerSyncChat(UUIDData.uuidUsername.get(event.getSender().getNameCard()), message);
//        }

        //执行文字命令
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && Boolean.parseBoolean(ConfigUtil.config.get(String.valueOf(event.getGroup().getId())).get("command"))) {
            CommandHandler commandHandler = new CommandHandler();
            commandHandler.CommandHandler(event);
        }
        return;
    }

    @EventHandler
    public void onMemberJoinRequest(MemberJoinRequestEvent event) {
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && minecraftintergration.contains(String.valueOf(event.getGroup().getId()))) {
            String answer = event.getMessage().split("：")[2];
            String uuid = NetworkMojang.MojangUserNameExist(answer);
            if (uuid != null) {
                id.put(event.getFromId(), answer);
                bedrock.put(event.getFromId(), false);
                event.accept();
                NetworkServer.ServerAddWhitelist(answer, event.getFromId());
                UUIDData.uuidUsername.put(answer, uuid);
            } else {
                HashMap<String, String> inviteCode = InviteCode.CheckInviteCode(answer, event.getFromId());
                if (Boolean.parseBoolean(inviteCode.get("result"))) {
                    String username = inviteCode.get("username");
                    id.put(event.getFromId(), username);
                    bedrock.put(event.getFromId(), true);
                    event.accept();
                    NetworkServer.ServerAddWhitelist(username, event.getFromId());
                    UUIDData.uuidUsername.put(answer, "bedrock");
                } else {
                    event.reject(false, "你和你的种族令我感到可笑");
                }
            }
        }
    }

    @EventHandler
    public void onMemberJoin(MemberJoinEvent event) {
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && Boolean.parseBoolean(ConfigUtil.config.get(String.valueOf(event.getGroup().getId())).get("giverank"))) {
            NormalMember member = event.getGroup().get(event.getMember().getId());
            member.setNameCard(id.get(event.getMember().getId()));
            if (bedrock.get(event.getMember().getId())) {
                member.setSpecialTitle(SpecialTitle.GetSpecialTitle(true));
            } else {
                member.setSpecialTitle(SpecialTitle.GetSpecialTitle(false));
            }
            return;
        }
    }

    @EventHandler
    public void onMemberLeave(MemberLeaveEvent event) {
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && minecraftintergration.contains(String.valueOf(event.getGroup().getId()))) {
            NetworkServer.ServerRemoveWhitelist(event.getMember().getId());
//            MessageChain messageChain = new MessageChainBuilder()
//                    .append(new PlainText(event.getMember().getNameCard() + "离开了我们,已冻结权限"))
//                    .build();
//            event.getGroup().sendMessage(messageChain);
            return;
        }
    }

    @EventHandler
    public void onMemberCardChange(MemberCardChangeEvent event) {
        if (ConfigUtil.config.get(String.valueOf(event.getGroup().getId())) != null && minecraftintergration.contains(String.valueOf(event.getGroup().getId()))) {
            if (event.getGroup().getBotPermission().equals(MemberPermission.ADMINISTRATOR) || event.getGroup().getBotPermission().equals(MemberPermission.OWNER)) {
                event.getGroup().get(event.getMember().getId()).setNameCard(event.getOrigin());
            }
        }
    }

    @EventHandler
    public void onBotInvitedJoinGroupRequest(BotInvitedJoinGroupRequestEvent event) {
        event.ignore();
    }

    @EventHandler
    public void onNewFriendRequest(NewFriendRequestEvent event) {
        event.reject(false);
    }
}
