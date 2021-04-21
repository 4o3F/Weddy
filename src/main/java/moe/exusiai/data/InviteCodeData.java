package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class InviteCodeData {
    public static HashMap<String, String> invitecode = new HashMap<>();

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void LoadInviteCodeData() {
        try {
            invitecode = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/invitecode.json"))), new TypeToken<HashMap<String, String>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveInviteCodeData() {
        String json = gson.toJson(invitecode);
        try {
            Files.write(Paths.get("./data/invitecode.json"), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
