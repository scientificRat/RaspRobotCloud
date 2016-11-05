package utility;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * Created by huangzhengyue on 2016/11/5.
 */
public class ShortMessageSender {
    private static final String url = "http://gw.api.taobao.com/router/rest";
    private static final String appKey = "23519469";
    private static final String secret = "38028b30ba9cd7d8c2f8ac17617e6a06";

    public static void sendVerifyingCode(String phoneNumber, String code) {
        TaobaoClient client = new DefaultTaobaoClient(url, appKey, secret);
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("");
        req.setSmsType("normal");
        req.setSmsFreeSignName("树莓派小车平台");
        req.setSmsParamString("{code:'"+code+"'}");
        req.setRecNum(phoneNumber);
        req.setSmsTemplateCode("SMS_25335220");
        AlibabaAliqinFcSmsNumSendResponse rsp = null;
        try {
            rsp = client.execute(req);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        sendVerifyingCode("18030848902","8923");
    }
}
