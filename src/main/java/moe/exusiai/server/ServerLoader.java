package moe.exusiai.server;

import fi.iki.elonen.util.ServerRunner;
import moe.exusiai.server.events.PlayerEvent;
import moe.exusiai.utils.ConfigUtil;

import java.util.ArrayList;

public class ServerLoader {
    public static String[] qqgroup = ConfigUtil.config.get("minecraft").get("group").split(",");
    public static void LoadServer() {
        RouteManage.allAction.add(PlayerEvent.class);
        RouteManage.RegisterRoute();
        (new Thread(() -> ServerRunner.run(ServerMain.class))).start();

    }
}
