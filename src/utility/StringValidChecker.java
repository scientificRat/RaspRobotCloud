package utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by huangzhengyue on 8/9/16.
 */
public class StringValidChecker {

    public static boolean isPasswordValid(String password){
        if(password.length()<8){
            return false;
        }
        return true;
    }
    public static boolean isUserNameValid(String userName){
        Pattern pattern = Pattern.compile("((?=[\\x21-\\x7e]+)[^A-Za-z0-9])");
        Matcher matcher = pattern.matcher(userName);
        if(matcher.find()){
            return false;
        }
        return true;
    }
    public static boolean isEmailValid(String email){
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
        Matcher matcher = pattern.matcher(email);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(isUserNameValid("@a###asdadsf"));
        System.out.println(isEmailValid("3124123afa@dsf.com"));
    }
}
