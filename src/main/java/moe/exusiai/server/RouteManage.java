package moe.exusiai.server;


import com.google.gson.Gson;
import moe.exusiai.server.annotations.ApiRoute;
import moe.exusiai.utils.JsonResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteManage {
    public static Map<String, Method> actionRouteMap = new HashMap<>();

    public static ArrayList<Class> allAction = new ArrayList<>();

    public static void RegisterRoute() {
        for (Class item : allAction) {
            Method[] methods = item.getDeclaredMethods();
            for (Method method : methods) {
                ApiRoute an = method.<ApiRoute>getAnnotation(ApiRoute.class);
                if (an != null)
                    actionRouteMap.put(an.Path().toLowerCase(), method);
            }
        }
    }

    public static String TouchAction(String url, Map jsonData) {
        Method action = TryGetAction(url);
        if (action == null)
            return "404";
        JsonResult result = null;
        try {
            if (action.getParameterCount() > 0) {
                result = (JsonResult)action.invoke(action.getDeclaringClass().newInstance(), new Object[] { jsonData });
            } else {
                result = (JsonResult)action.invoke(action.getDeclaringClass().newInstance(), new Object[0]);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return (new Gson()).toJson(result);
    }

    public static Method TryGetAction(String url) {
        if (actionRouteMap.containsKey(url))
            return actionRouteMap.get(url);
        return null;
    }
}
