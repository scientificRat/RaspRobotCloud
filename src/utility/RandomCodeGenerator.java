package utility;

import java.util.Random;

/**
 * Created by huangzhengyue on 8/31/16.
 */
public class RandomCodeGenerator {
    public static String getRandomString(String base,int length) { //length表示生成字符串的长度
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    //默认产生数字
    public static String getRandomString(int length) { //length表示生成字符串的长度
        return getRandomString("0123456789",length);
    }

    public static String getComplexRandomString(int length) { //length表示生成字符串的长度
        return getRandomString("0123456789abcdefghijklmnopqrsruvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",length);
    }
}
