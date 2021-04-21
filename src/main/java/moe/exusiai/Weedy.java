package moe.exusiai;

import moe.exusiai.data.DataUtils;
import moe.exusiai.listeners.FriendEventsListener;
import moe.exusiai.listeners.GroupEventsListener;
import moe.exusiai.server.ServerLoader;
import moe.exusiai.utils.ConfigUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;

public class Weedy {
    public static Bot bot;
    public static void main(String[] args) {
        DataUtils.LoadData();
        ConfigUtil.LoadConfig();
        ServerLoader.LoadServer();
        weedy();
    }
    public static void weedy() {
        Long qqnumber = Long.parseLong(ConfigUtil.config.get("account").get("id"));
        String qqpassword = ConfigUtil.config.get("account").get("password");

        final File logdictionary = new File("./logs");
        if (!logdictionary.exists()) {
            logdictionary.mkdirs();
        }

        bot = BotFactory.INSTANCE.newBot(qqnumber, qqpassword, new BotConfiguration() {
            {
                fileBasedDeviceInfo();
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                //redirectBotLogToDirectory(logdictionary);
                enableContactCache();
            }
        });
        bot.login();
        bot.getEventChannel().registerListenerHost(new FriendEventsListener());
        bot.getEventChannel().registerListenerHost(new GroupEventsListener());

        bot.join();
    }
}
