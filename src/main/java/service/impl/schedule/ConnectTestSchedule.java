
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ConnectTestSchedule {

    @Resource
    private TokenManager tokenManager;

    @Scheduled(cron = "0/5 * * * * *")
    public void connectTest(){
        Map<Integer, WebSocketServer> users = Container.getUserMap();
        Integer userId = null;
        WebSocketServer ws = null;
        if (users != null) {
            for (Map.Entry<Integer, WebSocketServer> entry : users.entrySet()) {
                userId = entry.getKey();
                ws = entry.getValue();
                if (tokenManager.getToken(userId + "") == null) {
                    if (ws != null) {
                        ws.onClose();
                        continue;
                    }
                }
                if (ws != null) {
                    long s = Math.abs(ws.getHeartbeatTime().getTime() - new Date().getTime()) / 1000;
                    if (s > 30) {
                        ws.onClose();
                    }
                }
            }
        }
    }

}
