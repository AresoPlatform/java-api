
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LockTakeMapper {
    
    int insertLockTakeRecord(LockTakeRecords lockTakeRecords);

    List<LockTakeRecords> select(@Param("tronAddress") String tronAddress,@Param("offset") int offset);

    LockTakeRecords newTakeRecord();

    LockTakeRecords getByTronAddress(@Param("tronAddress") String tronAddress);
	
    int updateLockTakeRecord(LockTakeRecords lockTakeRecords);
}
