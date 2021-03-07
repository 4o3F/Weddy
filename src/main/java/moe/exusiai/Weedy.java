package moe.exusiai;

import moe.exusiai.data.DataUtils;
import moe.exusiai.listeners.GroupEventsListener;
import moe.exusiai.utils.ConfigUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;

public class Weedy {
    public static void main(String[] args) {
        DataUtils.LoadData();
        ConfigUtil.LoadConfig();
        weedy();
    }
    public static void weedy() {
        Long qqnumber = Long.parseLong(ConfigUtil.config.get("account").get("id"));
        String qqpassword = ConfigUtil.config.get("account").get("password");

        final File logdictionary = new File("./logs");
        if (!logdictionary.exists()) {
            logdictionary.mkdirs();
        }

        Bot bot = BotFactory.INSTANCE.newBot(qqnumber, qqpassword, new BotConfiguration() {
            {
                fileBasedDeviceInfo();
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                //redirectBotLogToDirectory(logdictionary);
                enableContactCache();
            }
        });
        bot.login();
        bot.getEventChannel().registerListenerHost(new GroupEventsListener());
        bot.join();
    }
}
