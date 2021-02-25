
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/base")
@CrossOrigin
public class BaseController {

    @Resource
    private TokenManager tokenManager;

    @Resource
    private LockService lockService;

    @Resource
    private ReadContractSchedule readContractSchedule;

    @Resource
    private ReadLpDataSchedule readLpDataSchedule;


    @GetMapping("/serverTime")
    public ResultVO serverTime() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("serverTime", new Date().getTime());
        return ResultVO.returnSingleData(temp);
    }

    @GetMapping("/versionInfo")
    public ResultVO versionInfo() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("Android_Version", tokenManager.getConfigBykey("Android_Version"));
        temp.put("Android_Download_Url", tokenManager.getConfigBykey("Android_Download_Url"));
        temp.put("Android_Update_Content", tokenManager.getConfigBykey("Android_Update_Content"));
        temp.put("Android_Update_Date", tokenManager.getConfigBykey("Android_Update_Date"));
        temp.put("Android_Is_Force_Update", tokenManager.getConfigBykey("Android_Is_Force_Update"));
        temp.put("Android_Force_Update_Reason", tokenManager.getConfigBykey("Android_Force_Update_Reason"));
        return ResultVO.returnSingleData(temp);
    }

    @GetMapping("/pledgeInfo")
    public ResultVO pledgeInfo() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("txasContractAddress", PledgeConstant.contractAddress);
        temp.put("pountContractAddress", PledgeConstant.pountContractAddress);
        temp.put("asoTokenAddress", PledgeConstant.AsoTokenAddress);
        temp.put("poolDataAddress", PledgeConstant.poolDataAddress);

        temp.put("lpPoolDataAddress", PledgeConstant.LPPoolDataAddress);
        temp.put("lpPoolAddress", PledgeConstant.LPPoolAddress);
        temp.put("lpExchangeAddress", PledgeConstant.ExchangeAddresss);
        temp.put("valuesAggregatorAddress", PledgeConstant.valuesAggregatorAddress);


        temp.put("startLPBlock", PledgeConstant.startLPBlock);
        temp.put("decimals", PledgeConstant.decimals);
        temp.put("trxToEnergy",tokenManager.getConfigBykey("trxToEnergy"));
        temp.put("trxToNet",tokenManager.getConfigBykey("trxToNet"));
        return ResultVO.returnSingleData(temp);
    }

    @GetMapping("/getLockRate")
    public ResultVO getLockRate() {
        Map<String, Object> temp = new HashMap<>();
        temp.put("xasLockQuarter", tokenManager.getConfigBykey("xasLockQuarter"));
        temp.put("xasLockHalfYear", tokenManager.getConfigBykey("xasLockHalfYear"));
        temp.put("xasLockOneYear", tokenManager.getConfigBykey("xasLockOneYear"));
        temp.put("xasLockTwoYear", tokenManager.getConfigBykey("xasLockTwoYear"));
        temp.put("xasLockLimit", tokenManager.getConfigBykey("xasLockLimit"));
        temp.put("xasLockRate", tokenManager.getConfigBykey("xasLockRate"));
        return ResultVO.returnSingleData(temp);
    }
    @GetMapping("/getPledgeEnergy")
    public ResultVO getPledgeEnergy() {
        return lockService.getPledgeEnergy();
    }

    @GetMapping("/getWithdrawEnergy")
    public ResultVO getWithdrawEnergy() {
        return lockService.getWithdrawEnergy();
    }

    @GetMapping("/tronTransferEnergy")
    public ResultVO tronTransferEnergy(@RequestParam String type) {
        JSONObject value = new JSONObject();
        if (StringUtils.isEmpty(type)){
            value.put("energy","100000");
            value.put("net","345");
        } else {
            switch (type){
                case "trx":
                    value.put("energy",tokenManager.getConfigBykey("trxEnergy"));
                    value.put("net",tokenManager.getConfigBykey("trxNet"));
                    break;
                case "usdt":
                    value.put("energy",tokenManager.getConfigBykey("usdtEnergy"));
                    value.put("net",tokenManager.getConfigBykey("usdtNet"));
                    break;
                case "usdj":
                    value.put("energy",tokenManager.getConfigBykey("usdjEnergy"));
                    value.put("net",tokenManager.getConfigBykey("usdjNet"));
                    break;
                case "aso":
                    value.put("energy",tokenManager.getConfigBykey("asoEnergy"));
                    value.put("net",tokenManager.getConfigBykey("asoNet"));
                    break;
                case "trxToAso":
                    value.put("energy",tokenManager.getConfigBykey("trxToAsoEnergy"));
                    value.put("net",tokenManager.getConfigBykey("trxToAsoNet"));
                    break;
                case "asoToTrx":
                    value.put("energy",tokenManager.getConfigBykey("asoToTrxEnergy"));
                    value.put("net",tokenManager.getConfigBykey("asoToTrxNet"));
                    break;
                case "dealToRecipient":
                    value.put("energy",tokenManager.getConfigBykey("dealToRecipientEnergy"));
                    value.put("net",tokenManager.getConfigBykey("dealToRecipientNet"));
                    break;
                default:
                    value.put("energy","100000");
                    value.put("net","345");
                    break;
            }
        }
        return ResultVO.returnSingleData(value);
    }

    @GetMapping("/getAddLiquidityEnergy")
    public ResultVO getAddLiquidityEnergy(@RequestParam Integer type) {
        JSONObject res = new JSONObject();
        res.put("energy",tokenManager.getConfigBykey("addLiquidityEnergy"));
        res.put("net",tokenManager.getConfigBykey("addLiquidityNet"));
        if (type != null && type.intValue() == 2) {
            res.put("energy",tokenManager.getConfigBykey("removeLiquidityEnergy"));
            res.put("net",tokenManager.getConfigBykey("removeLiquidityNet"));
        }
        return ResultVO.returnSingleData(res);
    }

    @GetMapping("/getLpPledgeEnergy")
    public ResultVO getLpPledgeEnergy(@RequestParam Integer type) {
        return lockService.getLpPledgeEnergy(type);
    }

    @GetMapping("/getLpWithdrawEnergy")
    public ResultVO getLpWithdrawEnergy() {
        return lockService.getLpWithdrawEnergy();
    }

    @GetMapping("/getAllowance")
    public ResultVO getAllowance(@RequestParam String tronAddress) {
        return lockService.getAllowance(tronAddress);
    }


}