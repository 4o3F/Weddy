package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class MessageFilterData {
    private static HashMap<String, HashMap<String, Integer>> messagefilter = new HashMap<String, HashMap<String, Integer>>();
    public static HashMap<String, Integer> recallInTenMinutes = new HashMap<String, Integer>();
    public static HashMap<String, Integer> muteTime = new HashMap<String, Integer>();

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void LoadMessageFilterData() {
        try {
            messagefilter = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/messagefilter.json"))), new TypeToken<HashMap<String, HashMap<String, Integer>>>() {}.getType());
            recallInTenMinutes = messagefilter.get("recallInTenMinutes");
            muteTime = messagefilter.get("muteTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveMessageFilterData() {
        messagefilter.put("recallInTenMinutes", recallInTenMinutes);
        messagefilter.put("muteTime", muteTime);
        String json = gson.toJson(messagefilter);
        try {
            Files.write(Paths.get("./data/messagefilter.json"), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
