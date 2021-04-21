package moe.exusiai.handler.commandhandler;

import moe.exusiai.handler.invitecode.InviteCode;
import moe.exusiai.utils.ConfigUtil;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

public class CommandHandler {
    public static void CommandHandler(MessageEvent event) {
        String message = event.getMessage().contentToString();

            if (message.length() >= 6) {
                if (message.startsWith("创建注册码")) {
                    if (!event.getBot().getGroup(event.getSubject().getId()).get(event.getSender().getId()).getSpecialTitle().startsWith("R")) {
                        MessageChain messageChain = new MessageChainBuilder()
                                .append(new PlainText("警告!你无权访问权限文件!此行为已被记录,请等待下一步处理"))
                                .build();
                        ((GroupMessageEvent) event).getGroup().sendMessage(messageChain);
                    } else {
                        String usernameAndQQ = message.substring(6);
                        String username = usernameAndQQ.split(" ")[0];
                        Long qqnumber = Long.parseLong(usernameAndQQ.split(" ")[1]);
                        String invitecode = InviteCode.GenerateInviteCode(username, qqnumber);
                        MessageChain messageChain = new MessageChainBuilder()
                                .append(new At(event.getSender().getId()))
                                .append(new PlainText("\n" + username + "的注册码为\n" + invitecode))
                                .build();
                        ((GroupMessageEvent) event).getGroup().sendMessage(messageChain);
                    }
                }
            }


    }
}
