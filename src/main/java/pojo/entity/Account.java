


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Integer id;

    private String nickName;

    private String headImage;

    private String aschAddress;

    private String phone;

    private Integer accountStatus;

    private Date estoppelTime;

    private Date createtime;

    private Date updatetime;

    private String loginToken;
}
