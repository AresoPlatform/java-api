
import com.alibaba.fastjson.JSON;

import java.util.List;

public class ServerPublishUtil {


    public static String publishLiveMsg(Object msgBody){
        ServerPublishVO serverPublishVO = new ServerPublishVO(ServerPublishConstant.NOTICE_LIVE_TYPE, msgBody);
        return JSON.toJSONString(serverPublishVO);
    }

    public static String publishNoticeCouncilMsg(Object msgBody){
        ServerPublishVO serverPublishVO = new ServerPublishVO(ServerPublishConstant.NOTICE_COUNCIL_TYPE, msgBody);
        return JSON.toJSONString(serverPublishVO);
    }

    public static String publishAccountFundMsg(Object msgBody){
        ServerPublishVO serverPublishVO = new ServerPublishVO(ServerPublishConstant.FUND_COUNCIL_TYPE, msgBody);
        return JSON.toJSONString(serverPublishVO);
    }


    public static String publishMsg(Object msgBody){
        ServerPublishVO serverPublishVO = new ServerPublishVO(ServerPublishConstant.OUT_TYPE, msgBody);
        return JSON.toJSONString(serverPublishVO);
    }


    public static String publishBbsMsg(BbsSendVO bbsSendVO,int type) {
        ServerPublishVO serverPublishVO = new ServerPublishVO(type, bbsSendVO);
        return JSON.toJSONString(serverPublishVO);
    }

    public static String publishMsgStatistics(BbsSendWsVO sendWsVO) {
        ServerPublishVO serverPublishVO = new ServerPublishVO(ServerPublishConstant.BBS_STATISTICS_TYPE, sendWsVO);
        return JSON.toJSONString(serverPublishVO);
    }
}