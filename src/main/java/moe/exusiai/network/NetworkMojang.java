package moe.exusiai.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import moe.exusiai.utils.UUIDUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;

public class NetworkMojang {
    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    // https://wiki.vg/Mojang_API
    public static String MojangUserNameExist(String username) {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Integer code = response.code();
            String body = response.body().string();
            if (code == 204) {
                return null;
            } else if (code == 200) {
                HashMap<String, String> data = new HashMap<>();
                data = gson.fromJson(body, new TypeToken<HashMap<String, String>>() {}.getType());
                response.body().close();
                return UUIDUtil.noDashStringToUUID(data.get("id"));
            } else {
                response.body().close();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}