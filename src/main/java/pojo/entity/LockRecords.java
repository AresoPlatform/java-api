

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LockRecords {

    private Integer id;

    private Integer accountId;

    private String aschAddress;

    private String tronAddress;

    private BigDecimal num;

    private Long startHeight;

    private Date createTime;

    private Long endHeight;

    private Date endTime;

    private String transactionId;

    private BigDecimal txasNum;

    private String txasTransactionId;
}
