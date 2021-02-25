
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface LpPledgeMapper {

    int insertLpPledgeRecord(LpPledgeRecords lpPledgeRecords);

    List<LpPledgeRecords> selectByTron(@Param("tronAddress") String tronAddress, @Param("offset") int offset);

    LpPledgeRecords selectNewOne(@Param("type") Integer type);


    int webCountLpositionInfo(@Param("tronAddress") String tronAddress);


    List<LpPledgeRecords> webLpositionInfo(@Param("offset") int offset,
                                           @Param("tronAddress") String tronAddress,@Param("finalOrder") String finalOrder,
                                           @Param("desc")String desc);
}
