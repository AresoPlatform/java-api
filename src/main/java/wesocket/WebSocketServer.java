
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@ServerEndpoint("/connect/{userId}")
@Component
@Data
@Slf4j
@CrossOrigin
public class WebSocketServer {

    private Session session;

    private Date heartbeatTime;

    private RedisTokenManager redisTokenManager;

    private AccountMapper accountMapper;


    @OnOpen
    public void onOpen(@PathParam("userId") Integer userId, Session session) {

       
    }

    @OnClose
    public void onClose() {
        WebSocketServer ws = Container.removeUser(userId);
        if (ws!=null){
            log.info("onClose，accountId【{}】",userId);
            try {
                if (ws.session.isOpen()){
                    ws.session.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @OnError
    public void onError(Throwable error) {
    }

    @OnMessage
    public void onMessage(String message) {
        JSONObject msgJson = ClientRequestUtil.parseListenMsg(message);
        if (msgJson == null) {
            return;   
        }
        int messageType = msgJson.getInteger("message_type").intValue();

        if (messageType == ClientMsgConstant.HEARTBEAT_TEST_TYPE){ 
            heartbeatTime = new Date();
            heartbeatTest();
        }
    }


    public void sendMessage(String message) {
        if (session.isOpen()) {
            try {
                synchronized (session){
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void heartbeatTest() {
        sendMessage(ResponseClientUtil.returnSuccessMsg(ClientMsgConstant.HEARTBEAT_TEST_TYPE, ""));
    }
}