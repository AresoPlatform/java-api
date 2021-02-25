

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BbsAccountBO {

    private Integer id;

    private String nickName;

    private String aschAddress;

    private String secretkey;

    private String randomkey;

    private String dealPassword;

    private BigDecimal blockedBalances;
}
