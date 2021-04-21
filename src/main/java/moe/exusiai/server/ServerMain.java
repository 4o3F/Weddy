package moe.exusiai.server;


import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import moe.exusiai.network.NetworkServer;
import moe.exusiai.utils.ConfigUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ServerMain extends NanoHTTPD {
    public static ServerMain instance;

    public ServerMain(){
        super(Integer.parseInt(ConfigUtil.config.get("minecraft").get("listenport")));
        instance = this;
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, Object> jsonData;
        String result, uri = session.getUri();
        if (!uri.toLowerCase().contains("/api/server/getstatus")) {
            String clientAuthStr = (String) session.getHeaders().get("x-lode-authentication");
            if (clientAuthStr == null || clientAuthStr.equals(""))
                return newFixedLengthResponse((Response.IStatus) Response.Status.UNAUTHORIZED, "text/plain", "Error:  401 Unauthorized. 需要鉴权信息.");
            if ((clientAuthStr.split(";")).length != 2)
                return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 403 Forbidden. 鉴权信息不完整.");
            try {
                long ts = Long.parseLong(clientAuthStr.split(";")[1].split("=")[1].trim());
                if (!CheckTimestamp(ts))
                    return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 403 Forbidden. Timeout.");
                String serverAuthStr = GetAuthentication(uri);
                String token = clientAuthStr.split(";")[0].split("=")[1].trim();
                if (!serverAuthStr.equalsIgnoreCase(token))
                    return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 403 Forbidden. Authentication failed.");
            } catch (Exception e) {
                return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 403 Forbidden. 鉴权信息不正确. " + e.getMessage());
            }
        }
        if (uri.toLowerCase().contains("upload") || uri.toLowerCase().contains("install")) {
            jsonData = new HashMap<>();
            Map<String, File> allFile = UploadFiles(session);
            jsonData.put("files", allFile);
        } else {
            String body;
            try {
                body = GetBody(session.getInputStream());
            } catch (IOException e) {
                return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 500 INTERNAL ERROR. 请求体不正确. " + e.getMessage());
            }
            try {
                jsonData = (Map<String, Object>) (new Gson()).fromJson(body, Map.class);
            } catch (Exception e) {
                return newFixedLengthResponse((Response.IStatus) Response.Status.FORBIDDEN, "text/plain", "Error: 500 INTERNAL ERROR. 请求体格式不正确. " + e.getMessage());
            }
        }
        try {
            result = RouteManage.TouchAction(uri.toLowerCase(), jsonData);
        } catch (Exception e) {
            return newFixedLengthResponse((Response.IStatus) Response.Status.INTERNAL_ERROR, "text/plain", "");
        }
        if (result == "404")
            return newFixedLengthResponse((Response.IStatus) Response.Status.NOT_FOUND, "text/plain", "Error: " + uri + " is not found.");
        return newFixedLengthResponse((Response.IStatus) Response.Status.OK, "application/json; charset=utf-8", result);
    }

    private String GetAuthentication(String url) {
        return NetworkServer.generateToken(url);
    }

    private boolean CheckTimestamp(long ts) {
        System.out.println(Math.abs(System.currentTimeMillis() - ts) / 60000L <= 2L);
        System.out.println(System.currentTimeMillis());
        System.out.println(ts);
        System.out.println(System.currentTimeMillis() - ts);
        System.out.println(Math.abs(System.currentTimeMillis() - ts));
        System.out.println(Math.abs(System.currentTimeMillis() - ts)/6000L);
        return (Math.abs(System.currentTimeMillis() - ts) / 60000L <= 2L);
    }


    //NOT USABLE!!!!
    private Map<String, File> UploadFiles(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }
        Map<String, File> allFile = new HashMap<>();
//        for (String key : session.getParameters().keySet()) {
//            if (key.equalsIgnoreCase("data"))
//                continue;
//            String tmpFilePath = files.get(key);
//            String fileName = key;
//            File tmpFile = new File(tmpFilePath);
//            allFile.put(fileName, tmpFile);
//        }
        return allFile;
    }

    private String GetBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int count = inputStream.available();
        byte[] buffer = new byte[count];
        result.write(buffer, 0, inputStream.read(buffer));
        return result.toString("UTF-8");
    }
}
