
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LockTakeRecords {

    private Integer id;

    private String tronAddress;

    private BigDecimal num;
    private BigDecimal hashNum;

    private BigDecimal energyUsageTotal;
    private BigDecimal netUsage;
    private BigDecimal energyFee;

    private Long height;

    private Date createTime;
    private Date updateTime;

    private String transactionId;
}
