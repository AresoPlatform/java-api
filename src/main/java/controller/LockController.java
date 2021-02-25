

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/tron")
public class LockController {
    @Resource
    private LockService lockService;

    @GetMapping("/lpPledgeDo")
    public ResultVO lpPledgeDo(@RequestParam String tronAddress,@RequestParam String hash,HttpServletRequest request) {
        return lockService.lpPledgeDo(tronAddress,hash,AccountUtil.getLoginToken(request));
    }

    @GetMapping("/lpPledgeRecords")
    public ResultVO lpPledgeRecords(@RequestParam Integer offset,@RequestParam String tronAddress,HttpServletRequest request) {
        return lockService.lpPledgeRecords(tronAddress,offset,AccountUtil.getLoginToken(request));
    }

    @GetMapping("/lpTakeMineral")
    public ResultVO lpTakeMineral(@RequestParam String hash,@RequestParam String tronAddress) {
        return lockService.lpTakeMineral(hash, tronAddress);
    }

    @GetMapping("/lpTakeInfo")
    public ResultVO lpTakeInfo(@RequestParam Integer offset,@RequestParam String tronAddress) {
        return lockService.lpTakeInfo(offset,tronAddress);
    }


    @GetMapping("/getTronAddress")
    public ResultVO getTronAddress(HttpServletRequest request) {

        return lockService.getTronAddress(AccountUtil.getLoginToken(request));
    }

    @GetMapping("/setTronAddress")
    public ResultVO tronAddress(@RequestParam String tronAddress, HttpServletRequest request) {
        return lockService.tronAddress(tronAddress, AccountUtil.getLoginToken(request));
    }

    @GetMapping("/getLpositionInfo")
    public ResultVO getLpositionInfo(@RequestParam Integer offset,@RequestParam Integer type,HttpServletRequest request) {

        return lockService.getLpositionInfo(offset,type,AccountUtil.getLoginToken(request));
    }

    @GetMapping("/getBalance")
    public ResultVO getBalance(HttpServletRequest request) {
        return lockService.getBalance(AccountUtil.getLoginToken(request));
    }

    @GetMapping("/pledgeDo")
    public ResultVO pledgeDo(@RequestParam String tronAddress,@RequestParam String hash,HttpServletRequest request) {
        return lockService.pledgeDo(tronAddress,hash,AccountUtil.getLoginToken(request));
    }

    @GetMapping("/pledgeRecords")
    public ResultVO pledgeRecords(@RequestParam Integer offset,@RequestParam String tronAddress,HttpServletRequest request) {
        return lockService.pledgeRecords(tronAddress,offset,AccountUtil.getLoginToken(request));
    }

    @GetMapping("/takeMineral")
    public ResultVO takeMineral(@RequestParam String hash,@RequestParam String tronAddress) {
        return lockService.takeMineral(hash, tronAddress);
    }

    @GetMapping("/getTakeInfo")
    public ResultVO getTakeInfo(@RequestParam Integer offset,@RequestParam String tronAddress) {
        if (StringUtils.isEmpty(tronAddress)){
            return ResultVO.returnMsg(ResultCode.InvalidParam.getCode(),"波场地址为空");
        }
        return lockService.getTakeInfo(offset,tronAddress);
    }
}
