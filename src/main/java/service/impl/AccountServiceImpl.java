
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private TokenManager tokenManager;


    @Override
    public Account getAccountByPhone(String phone) {

        return accountMapper.getAccountByPhone(phone);
    }

    @Override
    public ResultVO accountInfo(String loginToken) {
        try {
            Account account = tokenManager.getAccountByToken(loginToken);
            if (account == null){
                return ResultVO.returnMsg(ResultCode.NotLogin);
            }

            String noticeKey = tokenManager.getConfigBykey(ConfigConstant.noticeKey);

            AccountVO vo = new AccountVO();
            vo.setHeadImage(StringUtils.isNotEmpty(account.getHeadImage())?account.getHeadImage():BBSConstant.deftHeadImage);
            vo.setPhone(account.getPhone());
            vo.setNickName(StringUtils.isEmpty(account.getNickName())?"":account.getNickName());
            vo.setAccountId(account.getId());
            vo.setAddress(account.getAschAddress()==null?"":account.getAschAddress());
            return ResultVO.returnSingleData(vo);

        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultVO.returnMsg(ResultCode.Failed);
    }


    @Override
    public boolean isHaveByPhone(String phone) {
        Account account = accountMapper.getAccountByPhone(phone);
        if (account == null){
            return true;
        }
        return false;
    }


    public boolean verifyCode(String invitationCode) {

        try {
            Account account = accountMapper.getAccountByCode(invitationCode.toLowerCase());
            if (account != null){
                return true;
            }
        }catch (Exception e){

        }

        return false;
    }



    @Override
    public int checkNickName(String nickName) {
        try {
            int count = accountMapper.checkNickName(nickName);
            if (count > 0){
                return count;
            } else {
                String data = HttpUtil.doGet(BBSConstant.STEEM_URL+BBSConstant.USERMETHOD+ BBSUtil.URLParmsEncode("[\""+nickName+"\"]"));
                if (StringUtils.isNotEmpty(data)){
                    JSONArray array = JSON.parseArray(data);
                    return array.size();
                } else {
                    return 1;
                }
            }
        }catch (Exception e){
        }
        return 1;
    }

}
