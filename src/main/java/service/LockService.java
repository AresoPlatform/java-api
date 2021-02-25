

public interface LockService {

    ResultVO tronAddress(String tronAddress,String loginToken);

    ResultVO getTronAddress(String loginToken);

    ResultVO getBalance(String loginToken);

    ResultVO takeMineral(String hash, String tronAddress);

    ResultVO getLpositionInfo(Integer offset, Integer type, String loginToken);

    ResultVO getTakeInfo(Integer offset, String tronAddress);

    ResultVO webLpositionInfo(Integer offset, String tronAddress, String aschAddress, Integer orderBy, Integer descOrAsc);

    ResultVO getPledgeEnergy();

    ResultVO getWithdrawEnergy();

    ResultVO pledgeDo(String tronAddress, String hash, String loginToken);

    ResultVO pledgeRecords(String tronAddress, Integer offset, String loginToken);

    ResultVO lpPledgeDo(String tronAddress, String hash, String loginToken);

    ResultVO lpPledgeRecords(String tronAddress, Integer offset, String loginToken);

    ResultVO lpTakeMineral(String hash, String tronAddress);

    ResultVO lpTakeInfo(Integer offset, String tronAddress);

    ResultVO webLpLpositionInfo(Integer offset, String tronAddress, Integer orderBy);

    ResultVO getLpPledgeEnergy(Integer type);

    ResultVO getLpWithdrawEnergy();

    ResultVO getAllowance(String tronAddress);
}
