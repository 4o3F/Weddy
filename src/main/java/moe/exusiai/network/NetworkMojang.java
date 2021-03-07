package moe.exusiai.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkMojang {
    // https://wiki.vg/Mojang_API
    public static boolean MojangUserNameExist(String username) {
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
                return false;
            } else if (code == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}