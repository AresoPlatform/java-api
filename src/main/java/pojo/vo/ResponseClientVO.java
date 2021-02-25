

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseClientVO {

    private Integer message_type;

    private Boolean success;

    private String msg;

}