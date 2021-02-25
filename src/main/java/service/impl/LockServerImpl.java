
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class LockServerImpl implements LockService {

    @Resource
    private TokenManager tokenManager;

    @Resource
    private LockMapper lockMapper;

    @Resource
    private LockTakeMapper lockTakeMapper;

    @Resource
    private LockPledgeMapper lockPledgeMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private ReadContractSchedule readContractSchedule;

    @Resource
    private ReadLpDataSchedule readLpDataSchedule;

    @Resource
    private LpTakeMapper lpTakeMapper;

    @Resource
    private LpPledgeMapper lpPledgeMapper;




    @Override
    public ResultVO lpPledgeDo(String tronAddress, String hash, String loginToken) {
        LpPledgeRecords lp = new LpPledgeRecords();
        lp.setPledgeHash(hash);
        lp.setPledgeTime(new Date());
        lp.setTronAddress(tronAddress);
        try {
            Account account = tokenManager.getAccountByToken(loginToken);
            if (account != null) lp.setAccountId(account.getId());

            String data = HttpUtil.doGet(PledgeConstant.validateTrx+hash);
            int count = 0;
            while (true) {
                if (count > 5){
                    break;
                }
                if (StringUtils.isNotEmpty(data) && data.length() > 5){
                    break;
                }
                data = HttpUtil.doGet(PledgeConstant.validateTrx+hash);
                count ++;
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                }
            }
            try {
                if (StringUtils.isNotEmpty(data)){
                    JSONObject object = JSON.parseObject(data);
                    lp.setPledgeTime(DateUtil.getDate(object.getLongValue("timestamp")));
                    lp.setTronAddress(object.getString("ownerAddress"));
                    lp.setPledgeHeight(object.getIntValue("block"));

                    JSONObject cost = object.getJSONObject("cost");
                    lp.setEnergyUsageTotal(cost.getBigDecimal("energy_usage_total"));
                    lp.setNetUsage(cost.getBigDecimal("net_usage"));

                    BigDecimal net_fee = cost.getBigDecimal("net_fee");
                    if (lp.getNetUsage() == null && net_fee == null){
                        lp.setNetUsage(BigDecimal.valueOf(345));
                    } else if (lp.getNetUsage() == null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lp.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN));
                    } else if (lp.getNetUsage() != null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lp.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN).add(lp.getNetUsage()));
                    }

                    object = object.getJSONObject("trigger_info");
                    String contract_address = object.getString("contract_address");
                    String method = object.getString("method");
                    if (PledgeConstant.LPPoolAddress.equals(contract_address)){
                        object = object.getJSONObject("parameter");
                        if (method.contains("stake")) {
                            lp.setType(1);
                            lp.setPledgeAmount(
                                    new BigDecimal(object.getString("_amount"))
                                            .divide(PledgeConstant.decimals,6,BigDecimal.ROUND_DOWN));
                        } else if (method.contains("repealPledge") || method.contains("exit")) {
                            lp.setType(2);
                            if (method.contains("repealPledge")){
                                lp.setPledgeAmount(
                                        new BigDecimal(object.getString("_repealAmount"))
                                                .divide(PledgeConstant.decimals,6,BigDecimal.ROUND_DOWN));
                            } else {
                                JSONObject agani = new JSONObject();
                                agani.put("value",hash);
                                data = HttpUtil.doPostJson(PledgeConstant.takeValidate,agani.toJSONString());
                                if (StringUtils.isNotEmpty(data)){
                                    agani = JSON.parseObject(data);
                                    agani = agani.getJSONArray("log").getJSONObject(0);
                                    lp.setPledgeAmount(new BigDecimal(Numeric.toBigInt(agani.getString("data"))));
                                }
                            }
                        } else {
                            lp.setType(3);
                            lp.setPledgeAmount(BigDecimal.valueOf(0));
                        }
                    }
                }
            }catch (Exception e){
            }
            lpPledgeMapper.insertLpPledgeRecord(lp);
            return ResultVO.returnMsg(ResultCode.Success);
        } catch (Exception e){
        }
        return ResultVO.returnMsg(ResultCode.Failed.getCode(),"");
    }

    @Override
    public ResultVO lpPledgeRecords(String tronAddress, Integer offset, String loginToken) {
        if (offset.intValue() <= 0) offset = 0;
        return ResultVO.returnSingleData(lpPledgeMapper.selectByTron(tronAddress,(offset-1)*10));
    }

    @Override
    public ResultVO lpTakeMineral(String hash, String tronAddress) {
        try {
            LpTakeRecords lp = new LpTakeRecords();
            lp.setTransactionId(hash);
            lp.setTronAddress(tronAddress);

            JSONObject object = new JSONObject();
            object.put("value",hash);
            String data = null;
            int count = 0;
            while (true) {
                if (count > 5){
                    break;
                }
                data = HttpUtil.doPostJson(PledgeConstant.takeValidate,object.toJSONString());
                if (StringUtils.isNotEmpty(data) && data.length() > 5){
                    break;
                }
                count ++;
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                }
            }

            try {
                if (StringUtils.isNotEmpty(data)){
                    object = JSON.parseObject(data);
                    lp.setHeight(object.getLongValue("blockNumber"));
                    lp.setCreateTime(DateUtil.getDate(object.getLongValue("blockTimeStamp")));
                    JSONObject receipt = object.getJSONObject("receipt");

                    BigDecimal temp = receipt.getBigDecimal("energy_fee");
                    lp.setEnergyFee(temp==null?BigDecimal.valueOf(0):temp);
                    lp.setEnergyUsageTotal(receipt.getBigDecimal("energy_usage_total"));
                    lp.setNetUsage(receipt.getBigDecimal("net_usage"));
                    BigDecimal net_fee = receipt.getBigDecimal("net_fee");
                    if (lp.getNetUsage() == null && net_fee == null){
                        lp.setNetUsage(BigDecimal.valueOf(279));
                    } else if (lp.getNetUsage() == null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lp.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN));
                    } else if (lp.getNetUsage() != null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lp.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN).add(lp.getNetUsage()));
                    }
                    object = object.getJSONArray("log").getJSONObject(0);
                    lp.setNum(new BigDecimal(Numeric.toBigInt(object.getString("data"))));
                }
            }catch (Exception e){
                lp.setNum(new BigDecimal("0"));
            }
            lpTakeMapper.insertLpTakeRecord(lp);

            return ResultVO.returnMsg(ResultCode.Success);
        } catch (Exception e){

        }
        return ResultVO.returnMsg(ResultCode.Failed.getCode(),"");
    }

    @Override
    public ResultVO lpTakeInfo(Integer offset, String tronAddress) {
        if (offset.intValue() <= 0) offset = 0;
        return ResultVO.returnSingleData(lpTakeMapper.select(tronAddress,(offset-1)*10));
    }



    @Override
    public ResultVO pledgeDo(String tronAddress, String hash, String loginToken) {
        try {
            LockPledgeRecords lockPledgeRecords = new LockPledgeRecords();
            lockPledgeRecords.setPledgeHash(hash);
            lockPledgeRecords.setPledgeTime(new Date());
            Account account = tokenManager.getAccountByToken(loginToken);
            String data = HttpUtil.doGet(PledgeConstant.validateTrx+hash);
            int count = 0;
            while (true) {
                if (count > 5){
                    break;
                }
                if (StringUtils.isNotEmpty(data) && data.length() > 5){
                    break;
                }
                data = HttpUtil.doGet(PledgeConstant.validateTrx+hash);
                count ++;
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                }
            }

            if (account != null){
                lockPledgeRecords.setAccountId(account.getId());
            }
            lockPledgeRecords.setTronAddress(tronAddress);
            try {
                if (StringUtils.isNotEmpty(data)){
                    JSONObject object = JSON.parseObject(data);
                    lockPledgeRecords.setTronAddress(object.getString("ownerAddress"));
                    lockPledgeRecords.setPledgeHeight(object.getIntValue("block"));
                    JSONObject cost = object.getJSONObject("cost");
                    lockPledgeRecords.setEnergyUsageTotal(cost.getBigDecimal("energy_usage_total"));
                    lockPledgeRecords.setNetUsage(cost.getBigDecimal("net_usage"));

                    BigDecimal net_fee = cost.getBigDecimal("net_fee");
                    if (lockPledgeRecords.getNetUsage() == null && net_fee == null){
                        lockPledgeRecords.setNetUsage(BigDecimal.valueOf(345));
                    } else if (lockPledgeRecords.getNetUsage() == null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lockPledgeRecords.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN));
                    } else if (lockPledgeRecords.getNetUsage() != null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lockPledgeRecords.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN).add(lockPledgeRecords.getNetUsage()));
                    }
                    object = object.getJSONObject("trigger_info");
                    object = object.getJSONObject("parameter");
                    if (PledgeConstant.pountContractAddress.equals(object.getString("recipient"))){
                        lockPledgeRecords.setPledgeAmount(
                                new BigDecimal(object.getString("amount"))
                                        .divide(PledgeConstant.decimals,6,BigDecimal.ROUND_DOWN));
                    }
                }
            }catch (Exception e){
            }
            lockPledgeMapper.insertLockPledgeRecord(lockPledgeRecords);
            count = lockPledgeMapper.isHave(tronAddress);
            
           
            return ResultVO.returnMsg(ResultCode.Success);
        }catch (Exception e){
        }

        return ResultVO.returnMsg(ResultCode.Failed.getCode(),"");
    }

    @Override
    public ResultVO pledgeRecords(String tronAddress, Integer offset, String loginToken) {
        return ResultVO.returnSingleData(lockPledgeMapper.selectByTron(tronAddress,(offset-1)*10));
    }

    @Override
    public ResultVO takeMineral(String hash, String tronAddress) {
        try {
            JSONObject object = new JSONObject();
            object.put("value",hash);
            String data = null;
            int count = 0;
            while (true) {
                if (count > 5){
                    break;
                }
                data = HttpUtil.doPostJson(PledgeConstant.takeValidate,object.toJSONString());
                if (StringUtils.isNotEmpty(data) && data.length() > 5){
                    break;
                }
                count ++;
                try {
                    Thread.sleep(20000);
                } catch (InterruptedException e) {
                }
            }

            LockTakeRecords lockTakeRecords = new LockTakeRecords();
            lockTakeRecords.setTransactionId(hash);
            lockTakeRecords.setTronAddress(tronAddress);
            try {
                if (StringUtils.isNotEmpty(data)){
                    object = JSON.parseObject(data);
                    lockTakeRecords.setHeight(object.getLongValue("blockNumber"));
                    lockTakeRecords.setCreateTime(DateUtil.getDate(object.getLongValue("blockTimeStamp")));
                    JSONObject receipt = object.getJSONObject("receipt");

                    BigDecimal temp = receipt.getBigDecimal("energy_fee");
                    lockTakeRecords.setEnergyFee(temp==null?BigDecimal.valueOf(0):temp);
                    lockTakeRecords.setEnergyUsageTotal(receipt.getBigDecimal("energy_usage_total"));
                    lockTakeRecords.setNetUsage(receipt.getBigDecimal("net_usage"));

                    BigDecimal net_fee = receipt.getBigDecimal("net_fee");
                    if (lockTakeRecords.getNetUsage() == null && net_fee == null){
                        lockTakeRecords.setNetUsage(BigDecimal.valueOf(279));
                    } else if (lockTakeRecords.getNetUsage() == null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lockTakeRecords.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN));
                    } else if (lockTakeRecords.getNetUsage() != null && net_fee != null){
                        net_fee = net_fee.divide(PledgeConstant.decimals);
                        BigDecimal trxToNet = new BigDecimal(tokenManager.getConfigBykey("trxToNet"));
                        lockTakeRecords.setNetUsage(net_fee.divide(trxToNet,1,BigDecimal.ROUND_DOWN).add(lockTakeRecords.getNetUsage()));
                    }
                    object = object.getJSONArray("log").getJSONObject(0);
                    lockTakeRecords.setNum(new BigDecimal(Numeric.toBigInt(object.getString("data"))));
                    lockTakeRecords.setHashNum(new BigDecimal(Numeric.toBigInt(object.getString("data"))));
                }
            }catch (Exception e){
                lockTakeRecords.setNum(new BigDecimal("0"));
                lockTakeRecords.setHashNum(lockTakeRecords.getNum());
            }
            lockTakeMapper.insertLockTakeRecord(lockTakeRecords);

            return ResultVO.returnMsg(ResultCode.Success);
        }catch (Exception e){
        }

        return ResultVO.returnMsg(ResultCode.Failed.getCode(),"");
    }

    @Override
    public ResultVO getTakeInfo(Integer offset, String tronAddress) {

        return ResultVO.returnSingleData(lockTakeMapper.select(tronAddress,(offset-1)*10));
    }

    public ResultVO lockedPosition(String transactionId, String loginToken) {
        

    }


    @Override
    public ResultVO getLpositionInfo(Integer offset,Integer type, String loginToken) {
        List<LockRecords> lockRecords = new ArrayList<>();
        try {
            Account account = tokenManager.getAccountByToken(loginToken);
            if (account == null){
                return ResultVO.returnMsg(ResultCode.NotLogin);
            }
            if (offset == null ||offset.intValue() == 0){
                offset = 1;
            }
            if (type.intValue() == 0){
                lockRecords = lockMapper.getLpositionInfo0(account.getId(),
                        (offset-1)*10
                );
            } else {
                lockRecords = lockMapper.getLpositionInfo1(account.getId(),
                        (offset-1)*10
                );
            }
        }catch (Exception e){
        }
        return ResultVO.returnSingleData(lockRecords);
    }

    @Override
    public ResultVO webLpLpositionInfo(Integer offset, String tronAddress, Integer orderBy) {
        String order = "pledgeTime";
        String desc = "DESC";
        if (offset == null || offset.intValue() <= 0){
            offset = 1;
        }
        if (orderBy != null && orderBy.intValue() == 2){
            order = "num";
        }

        Integer finalOffset = offset;
        String finalOrder = order;
        PageTableRequest request = new PageTableRequest();
        request.setOffset(offset);
        request.setLimit(10);
        PageTableResponse msg = new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return lpPledgeMapper.webCountLpositionInfo(tronAddress);
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<LpPledgeRecords> list(PageTableRequest request) {
                return lpPledgeMapper.webLpositionInfo((finalOffset -1)*10,tronAddress,finalOrder, desc);
            }
        }).handle(request);
        return ResultVO.returnSingleData(msg);
    }

    @Override
    public ResultVO webLpositionInfo(Integer offset, String tronAddress, String aschAddress, Integer orderBy, Integer descOrAsc) {
        String order = "createTime";
        String desc = "DESC";
        if (descOrAsc != null && descOrAsc.intValue() == 2){
            desc = "ASC";
        }
        if (offset == null || offset.intValue() <= 0){
            offset = 1;
        }
        if (orderBy != null && orderBy.intValue() == 2){
            order = "num";
        }
        if (orderBy != null && orderBy.intValue() == 3){
            order = "txasNum";
        }

        Integer finalOffset = offset;
        String finalOrder = order;
        PageTableRequest request = new PageTableRequest();
        request.setOffset(offset);
        request.setLimit(10);
        String finalDesc = desc;
        PageTableResponse msg = new PageTableHandler(new PageTableHandler.CountHandler() {

            @Override
            public int count(PageTableRequest request) {
                return lockMapper.webCountLpositionInfo(tronAddress,aschAddress);
            }
        }, new PageTableHandler.ListHandler() {

            @Override
            public List<LockRecords> list(PageTableRequest request) {
                return lockMapper.webLpositionInfo((finalOffset -1)*10,tronAddress,aschAddress,finalOrder, finalDesc);
            }
        }).handle(request);
        return ResultVO.returnSingleData(msg);
    }
       

    @Override
    public ResultVO tronAddress(String tronAddress,String loginToken) {
        Account account = tokenManager.getAccountByToken(loginToken);
        if (account == null){
            return ResultVO.returnMsg(ResultCode.NotLogin);
        }
        int count = lockMapper.checkAddress(account.getId(),tronAddress);
        count = lockMapper.setTronAddress(tronAddress,account.getId());
        if (count > 0){
            return ResultVO.returnMsg(ResultCode.Success);
        }
        return ResultVO.returnMsg(ResultCode.Failed.getCode(),"");
    }

    @Override
    public ResultVO getTronAddress(String loginToken) {
        Account account = tokenManager.getAccountByToken(loginToken);
        if (account == null){
            return ResultVO.returnMsg(ResultCode.NotLogin);
        }
        String tronAddress = lockMapper.getTronInfo(account.getId());
        return ResultVO.returnSingleData(StringUtils.isEmpty(tronAddress)?"":tronAddress);
    }

    @Override
    public ResultVO getPledgeEnergy() {

        JSONObject res = new JSONObject();
        res.put("energy_usage_total",BigDecimal.valueOf(300000));
        res.put("net_usage",BigDecimal.valueOf(500));
        res.put("isEnoughEnergy",false);
        try {
            LockPledgeRecords lock = lockPledgeMapper.selectNewOne();
            if (lock != null){
                res.put("energy_usage_total",lock.getEnergyUsageTotal());
                res.put("net_usage",lock.getNetUsage());
            } else {
                String data = HttpUtil.doGet(PledgeConstant.pledgeEnergy);
                if (StringUtils.isNotEmpty(data)){
                    JSONObject object = JSON.parseObject(data);
                    if (object.getIntValue("rangeTotal")>0){
                        JSONObject cost = object.getJSONArray("data").getJSONObject(0).getJSONObject("cost");
                        BigDecimal net_usage = cost.getBigDecimal("net_usage");
                        if (net_usage == null || net_usage.compareTo(BigDecimal.valueOf(0)) == 0){
                            net_usage = new BigDecimal("345");
                        }
                        res.put("energy_usage_total",
                                cost.getBigDecimal("energy_usage_total").
                                        add(cost.getBigDecimal("energy_usage_total").multiply(new BigDecimal("0.1"))));
                        res.put("net_usage",net_usage.add(net_usage.multiply(new BigDecimal("0.1"))));
                    }
                }
            }
            String data = HttpUtil.doGet(PledgeConstant.tronAccount+PledgeConstant.txasAddress);
            if (StringUtils.isNotEmpty(data)){
                JSONObject object = JSON.parseObject(data);
                object = object.getJSONObject("bandwidth");
                if (object.getBigDecimal("energyRemaining").compareTo(res.getBigDecimal("energy_usage_total"))>0){
                    res.put("isEnoughEnergy",true);
                }
            }
        }catch (Exception e){
        }
        return ResultVO.returnSingleData(res);
    }

    @Override
    public ResultVO getWithdrawEnergy() {
        JSONObject res = new JSONObject();
        res.put("energy_usage_total",BigDecimal.valueOf(300000));
        res.put("net_usage",BigDecimal.valueOf(500));
        LockTakeRecords record = lockTakeMapper.newTakeRecord();
        if (record != null){
            res.put("energy_usage_total",
                    record.getEnergyUsageTotal().
                            add(record.getEnergyUsageTotal().multiply(new BigDecimal("0.1"))));
            res.put("net_usage",record.getNetUsage().add(record.getNetUsage().multiply(new BigDecimal("0.1"))));
        }
        return ResultVO.returnSingleData(res);
    }

    @Override
    public ResultVO getLpPledgeEnergy(Integer type) {
        JSONObject res = new JSONObject();
        res.put("energy_usage_total",BigDecimal.valueOf(200000));
        res.put("net_usage",BigDecimal.valueOf(500));
        res.put("isEnoughEnergy",false);
        try {
            LpPledgeRecords lock = lpPledgeMapper.selectNewOne(type);
            if (lock != null){
                res.put("energy_usage_total",lock.getEnergyUsageTotal().add(lock.getEnergyUsageTotal().multiply(new BigDecimal("0.1"))));
                res.put("net_usage",lock.getNetUsage().add(lock.getNetUsage().multiply(new BigDecimal("0.1"))));
            }
        }catch (Exception e){
        }
        return ResultVO.returnSingleData(res);
    }

    @Override
    public ResultVO getLpWithdrawEnergy() {
        JSONObject res = new JSONObject();
        res.put("energy_usage_total",BigDecimal.valueOf(200000));
        res.put("net_usage",BigDecimal.valueOf(500));
        LpTakeRecords record = lpTakeMapper.newTakeRecord();
        if (record != null){
            res.put("energy_usage_total",
                    record.getEnergyUsageTotal().
                            add(record.getEnergyUsageTotal().multiply(new BigDecimal("0.1"))));
            res.put("net_usage",record.getNetUsage().add(record.getNetUsage().multiply(new BigDecimal("0.1"))));
        }
        return ResultVO.returnSingleData(res);
    }

    @Override
    public ResultVO getAllowance(String tronAddress) {
        String amount = TronUtil.allowance(tronAddress,PledgeConstant.LPPoolAddress);
        if (amount != null){
            return ResultVO.returnSingleData(amount);
        }
        return ResultVO.returnSingleData("0");
    }
}
