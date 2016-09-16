package utility;

/**
 * Created by huangzhengyue on 8/15/16.
 */
public class GeneralJsonBuilder {
    public static String error(String info){
        StringBuilder stringBuilder= new StringBuilder();
        stringBuilder.append("{\"error\":\"");
        stringBuilder.append(info);
        stringBuilder.append("\"}");
        return stringBuilder.toString();
    }
    public static String succuss(boolean success){
        if(success){
            return "{\"success\":true}";
        }
        else {
            return "{\"success\":false}";
        }
    }
}
