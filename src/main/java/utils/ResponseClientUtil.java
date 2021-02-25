
import com.alibaba.fastjson.JSON;

public class ResponseClientUtil {


    public static String returnSuccessMsg(Integer msgType, String msg){
        ResponseClientVO responseClientVO = new ResponseClientVO(msgType, true, msg);
        return JSON.toJSONString(responseClientVO);
    }

    public static String returnFailedMsg(Integer msgType, String msg){
        ResponseClientVO responseClientVO = new ResponseClientVO(msgType, false, msg);
        return JSON.toJSONString(responseClientVO);
    }

}