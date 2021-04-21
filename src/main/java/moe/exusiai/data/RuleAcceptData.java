package moe.exusiai.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class RuleAcceptData {
    public static ArrayList<Long> ruleUnaccepted = new ArrayList<Long>();

    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static void LoadRuleAcceptData() {
        try {
            ruleUnaccepted = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/ruleaccept.json"))), new TypeToken<ArrayList<Long>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveRuleAcceptData() {
        String json = gson.toJson(ruleUnaccepted);
        try {
            Files.write(Paths.get("./data/ruleaccept.json"), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
