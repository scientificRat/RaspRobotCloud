package utility;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by huangzhengyue on 16/9/6.
 */
public class UniqueIdGenerator {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    public static String generate(){
        return System.currentTimeMillis() + (sequence.getAndIncrement()%90+10) + RandomCodeGenerator.getComplexRandomString(7);
    }
    //test
    public static void main(String[] args) {
        for(int i=0;i<1000;i++){
            System.out.println(generate());
        }
    }
}
