
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ConfigMapper {

    List<Config> getConfig();

    List<Config> getConfigPage(@Param("page")Integer page);

    int getConfigCount();

    int setConfig(ConfigBO configBO);
}
