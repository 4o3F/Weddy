package moe.exusiai.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moe.exusiai.utils.ConfigUtil;
import okhttp3.*;
import sun.security.provider.SHA;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class NetworkServer {
    private static String endpoint = ConfigUtil.config.get("minecraft").get("pushurl");
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    private static String username = ConfigUtil.config.get("minecraft").get("username");
    private static String password = ConfigUtil.config.get("minecraft").get("password");

    public static String generateToken(String apiUrl) {

        String input = username + apiUrl.toLowerCase() + password;
        String token = "";

        try {
            // SHA 加密开始
            // 创建加密对象 并傳入加密類型
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            // 传入要加密的字符串
            messageDigest.update(input.getBytes());
            // 得到 byte 類型结果
            byte byteBuffer[] = messageDigest.digest();

            // 將 byte 轉換爲 string
            StringBuffer strHexString = new StringBuffer();
            // 遍歷 byte buffer
            for (int i = 0; i < byteBuffer.length; i++) {
                String hex = Integer.toHexString(0xff & byteBuffer[i]);
                if (hex.length() == 1) {
                    strHexString.append('0');
                }
                strHexString.append(hex);
            }
            // 得到返回結果
            token = strHexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static void ServerAddWhitelist(String username, Long qqnumber) {
        String apiUrl = "/api/Server/ExecuteCommand";
        String url = endpoint + apiUrl;

        String token = generateToken(apiUrl);
        String header = "token=" + token + "; ts=" + Long.toString(System.currentTimeMillis() / 1000L);
        HashMap<String, String> data = new HashMap<>();
        data.put("command", "ml add " + username + " " + qqnumber);
        String json = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("X-Lode-Authentication", header)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Integer code = response.code();
            if (code == 403) {
                System.out.println("error" + response.body().string());
            }
            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ServerRemoveWhitelist(Long qqnumber) {
        String apiUrl = "/api/Server/ExecuteCommand";
        String url = endpoint + apiUrl;

        String token = generateToken(apiUrl);
        String header = "token=" + token + "; ts=" + Long.toString(System.currentTimeMillis());
        HashMap<String, String> data = new HashMap<>();
        data.put("command", "ml qremove " + qqnumber);
        String json = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("X-Lode-Authentication", header)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Integer code = response.code();
            if (code == 403) {
                System.out.println("error" + response.body().string());
            }
            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //暂时关闭，等待我重写Lode.Api
    public static void ServerSyncChat(String uuid, String message) {
        String apiUrl = "/api/User/SendChat";
        String url = endpoint + apiUrl;

        String token = generateToken(apiUrl);
        String header = "token=" + token + "; ts=" + Long.toString(System.currentTimeMillis());
        HashMap<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("message", message);
        String json = gson.toJson(data);
        RequestBody body = RequestBody.create(JSON, json);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("X-Lode-Authentication", header)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Integer code = response.code();
            if (code != 200) {
                System.out.println("error" + response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
