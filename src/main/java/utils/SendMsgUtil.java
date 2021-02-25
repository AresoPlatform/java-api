

import java.util.Map;

public class SendMsgUtil {

    public static void publishAllAccount(Object msg,Map<Integer,Object> exp){
        for(Map.Entry<Integer, WebSocketServer> a: Container.getUserMap().entrySet()){
            if (exp!=null && exp.size()>0 && exp.containsKey(a.getKey())){
                continue;
            }
            if (a.getValue() != null){
                a.getValue().sendMessage(ServerPublishUtil.publishLiveMsg(msg));
            }
        }
    }

    public static void publishPointNoticeAccount(Object msg,Map<Integer,WebSocketServer> map){
        for(Map.Entry<Integer, WebSocketServer> a: map.entrySet()){
            if (a.getValue() != null){
                a.getValue().sendMessage(ServerPublishUtil.publishNoticeCouncilMsg(msg));
            }
        }
    }


    public static void publishPointFundAccount(Object msg,Map<Integer,WebSocketServer> map){
        for(Map.Entry<Integer, WebSocketServer> a: map.entrySet()){
            if (a.getValue() != null){
                a.getValue().sendMessage(ServerPublishUtil.publishAccountFundMsg(msg));
            }
        }
    }
}
