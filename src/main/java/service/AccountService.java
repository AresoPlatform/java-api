
import java.math.BigDecimal;

public interface AccountService {
    Account getAccountByPhone(String phone);

    boolean isHaveByPhone(String phone);

    ResultVO accountInfo(String loginToken);

    int checkNickName(String nickName);

}
