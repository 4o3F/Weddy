package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class UUIDData {
    public static HashMap<String, String> uuidUsername = new HashMap<>();

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void LoadUUIDData() {
        try {
            uuidUsername = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/uuid.json"))), new TypeToken<HashMap<String, String>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveUUIDData() {
        String json = gson.toJson(uuidUsername);
        try {
            Files.write(Paths.get("./data/uuid.json"), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
