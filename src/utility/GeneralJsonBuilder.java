package utility;

import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by huangzhengyue on 8/15/16.
 */
public class GeneralJsonBuilder {
    private static Gson gson = new Gson();

    public static String error(String info) {
        HashMap<String, Object> rtn = new HashMap<>();
        rtn.put("error", info);
        rtn.put("success", false);
        return gson.toJson(rtn);
    }

    public static String success(boolean success) {
        if (success) {
            return "{\"success\":true}";
        } else {
            return "{\"success\":false}";
        }
    }
}
