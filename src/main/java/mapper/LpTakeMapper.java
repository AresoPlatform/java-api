
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LpTakeMapper {

    int insertLpTakeRecord(LpTakeRecords LpTakeRecords);

    List<LpTakeRecords> select(@Param("tronAddress") String tronAddress, @Param("offset") int offset);

    LpTakeRecords newTakeRecord();

    LpTakeRecords getByTronAddress(@Param("tronAddress") String tronAddress);
}
