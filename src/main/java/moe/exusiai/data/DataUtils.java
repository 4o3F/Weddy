package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataUtils {
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public static void LoadData() {
        File datafolder = new File("./data");
        if (!datafolder.exists()) {
            datafolder.mkdirs();
        }

        MessageFilterData.LoadMessageFilterData();

        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SaveData();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public static void SaveData() {
        MessageFilterData.SaveMessageFilterData();
    }
}
