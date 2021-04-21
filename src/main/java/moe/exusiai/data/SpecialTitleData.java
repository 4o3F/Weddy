package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SpecialTitleData {
    public static HashMap<String, Integer> specialrank = new HashMap<>();

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void LoadSpecialTitleData() {
        try {
            specialrank = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/specialrank.json"))), new TypeToken<HashMap<String, Integer>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveSpecialTitleData() {
        String json = gson.toJson(specialrank);
        try {
            Files.write(Paths.get("./data/specialrank.json"), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
