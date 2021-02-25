
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/web/tron")
public class WebLockController {
    @Resource
    private LockService lockService;

    @GetMapping("/webLpositionInfo")
    public ResultVO webLpositionInfo(@RequestParam Integer offset,
                                     @RequestParam(required = false) String tronAddress,
                                     @RequestParam(required = false) String aschAddress,@RequestParam(required = false) Integer orderBy,@RequestParam(required = false) Integer descOrAsc) {
        return lockService.webLpositionInfo(offset,tronAddress,aschAddress,orderBy,descOrAsc);
    }

    @GetMapping("/webLpLpositionInfo")
    public ResultVO webLpLpositionInfo(@RequestParam Integer offset,
                                     @RequestParam(required = false) String tronAddress
                                    ,@RequestParam(required = false) Integer orderBy) {
        return lockService.webLpLpositionInfo(offset,tronAddress,orderBy);
    }

    @GetMapping("/getDefiInfo")
    public ResultVO getDefiInfo() {
        return lockService.getDefiInfo();
    }

}
