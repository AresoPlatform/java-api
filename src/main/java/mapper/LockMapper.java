
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface LockMapper {

    int setTronAddress(@Param("tronAddress") String tronAddress,@Param("accountId") int accountId);

    String getTronInfo(@Param("id") Integer id);

    int checkAddress(@Param("id") Integer id,@Param("tronAddress") String tronAddress);

    int insertLockRecord(LockRecords lockRecords);

    int setTxasTransactionId(@Param("id") Integer id,
                             @Param("txasTransactionId") String txasTransactionId,
                             @Param("txasNum") BigDecimal txasNum);

    List<LockRecords> getRepealAll();

    List<LockRecords> selectMax(@Param("aschAddress") String aschAddress);

    List<LockRecords> getLpositionInfo0(@Param("accountId") Integer accountId, @Param("offset") Integer offset);

    List<LockRecords> getLpositionInfo1(@Param("accountId") Integer accountId, @Param("offset") Integer offset);

    List<LockRecords> getBufaTxas();


    List<LockRecords> webLpositionInfo(@Param("offset") int offset,@Param("tronAddress") String tronAddress,@Param("aschAddress") String aschAddress,@Param("orderType") String order,@Param("descType") String desc);


    int webCountLpositionInfo(@Param("tronAddress") String tronAddress,@Param("aschAddress") String aschAddress);



}
