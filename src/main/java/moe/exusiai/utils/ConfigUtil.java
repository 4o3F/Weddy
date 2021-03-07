package moe.exusiai.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ConfigUtil {
    public static HashMap<String, HashMap<String, String>> config = null;
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public static void LoadConfig() {
        try {
            String json = new String(Files.readAllBytes(Paths.get("./config.json")));
            config = gson.fromJson(json, new TypeToken<HashMap<String, HashMap<String, String>>>() { }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
