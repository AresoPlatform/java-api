

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LpPledgeRecords {

    private Integer id;
    private Integer accountId;
    private Integer pledgeHeight;

    private String tronAddress;

    private BigDecimal pledgeAmount;

    private Date pledgeTime;

    private String pledgeHash;

    private BigDecimal energyUsageTotal;
    private BigDecimal netUsage;

    private Integer type;
}
