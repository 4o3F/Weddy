package moe.exusiai.server.events;

import moe.exusiai.Weedy;
import moe.exusiai.server.ServerLoader;
import moe.exusiai.server.annotations.ApiRoute;
import moe.exusiai.utils.JsonResult;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class PlayerEvent {
    @ApiRoute(Path = "/api/Player/PlayerChat")
    public JsonResult PlayerChatEvent(Map data) {
        String name = data.get("Player").toString();
        String message = data.get("Message").toString();

        MessageChain messageChain = new MessageChainBuilder()
                .append(new PlainText("[" + name + "] " + message))
                .build();
        Iterator<String> iterator = Arrays.asList(ServerLoader.qqgroup).iterator();
        while (iterator.hasNext()) {
            Weedy.bot.getGroup(Long.parseLong(iterator.next())).sendMessage(messageChain);
        }
        return new JsonResult();
    }
}
