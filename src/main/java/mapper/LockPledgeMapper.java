
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface LockPledgeMapper {

    int insertLockPledgeRecord(LockPledgeRecords lockPledgeRecords);

    List<LockPledgeRecords> selectValidPledgeById(@Param("tronAddress") String tronAddress);

    List<LockPledgeRecords> selectByTron(@Param("tronAddress") String tronAddress,@Param("offset") int offset);

    LockPledgeRecords selectNewOne();
}
