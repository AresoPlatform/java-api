
import com.huobi.client.model.TradeStatistics;
import io.broker.api.client.domain.market.TickerStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Component
@Slf4j
public class DrawLimitSchedule {

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private TokenManager tokenManager;

    public void updateDrawLimit(){
        try {
            TickerStatistics tickerStatistics = Container.getJbInstance().get24HrPriceStatistics("XASUSDT");
            TradeStatistics statistics = Container.getHuobiInstance().get24HTradeStatistics(ExchangeConstant.SYMBOL_STEEM);
            if (tickerStatistics != null){
                BigDecimal low = new BigDecimal(tickerStatistics.getLowPrice());
                BigDecimal steemCount = new BigDecimal("2.1").divide(statistics.getLow(),3,BigDecimal.ROUND_DOWN);
                BigDecimal drawLimit = new BigDecimal("2.1").divide(low,3,BigDecimal.ROUND_DOWN);
                if (drawLimit.compareTo(new BigDecimal("0")) > 0){
                    BigDecimal old = new BigDecimal(tokenManager.getConfigBykey("drawLimit"));
                    BigDecimal steem = new BigDecimal(tokenManager.getConfigBykey("steemDrawLimit"));
                    BigDecimal xasLow = new BigDecimal(tokenManager.getConfigBykey("xas24HLow"));
                    BigDecimal steemLow = new BigDecimal(tokenManager.getConfigBykey("steem24HLow"));
                    boolean flag = false;
                    if (old.compareTo(new BigDecimal((drawLimit.intValue()+1)+"")) != 0){
                        flag = true;
                        configMapper.setDrawLimit((drawLimit.intValue()+1)+"");
                    }
                    if (steem.compareTo(steemCount) != 0){
                        flag = true;
                        configMapper.setSteemDrawLimit(steemCount.toString());
                    }
                    if (xasLow.compareTo(low) != 0){
                        flag = true;
                        configMapper.setXasLow(tickerStatistics.getLowPrice());
                    }
                    if (steemLow.compareTo(statistics.getLow()) != 0){
                        flag = true;
                        configMapper.setSteemLow(statistics.getLow().toString());
                    }
                    if (flag){
                        tokenManager.saveConfig();
                    }
                }
            }
        } catch (Exception e){
        }

    }

}
