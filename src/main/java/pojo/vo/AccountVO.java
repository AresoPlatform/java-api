

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountVO {

    private Integer accountId;

    private String phone;

    private String nickName;

    private String headImage;

    private String address;

    private String balance;


    private Boolean isDeal;


    private String estoppelTime;



}
