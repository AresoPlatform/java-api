

import lombok.Data;

@Data
public class ServerPublishVO {
    private Integer publish_type;
    private Integer action;
    private Object msg_body;

    public ServerPublishVO(Integer publish_type, Object msg_body) {
        this.publish_type = publish_type;
        this.msg_body = msg_body;
    }

    public ServerPublishVO(Integer publish_type, Integer msg_type, Object msg_body) {
        this.publish_type = publish_type;
        this.action = msg_type;
        this.msg_body = msg_body;
    }

}