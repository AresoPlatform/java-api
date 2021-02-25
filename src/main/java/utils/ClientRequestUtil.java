
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class ClientRequestUtil {

    public static JSONObject parseListenMsg(String message){
        if(StringUtils.isEmpty(message))
            return null;
        JSONObject msgJson = null;
        try {
            msgJson = JSONObject.parseObject(message);
        }catch (Exception e) {
            return null;
        }
        if(msgJson == null)
            return null;
        Integer messageType = null;
        try {
            messageType = msgJson.getInteger("message_type");
        }catch (Exception e) {
            return null;
        }
        if(messageType == null)
            return null;
        return msgJson;
    }

}