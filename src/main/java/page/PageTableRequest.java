

import lombok.Data;

import java.io.Serializable;
import java.util.Map;


@Data
public class PageTableRequest implements Serializable {

	private static final long serialVersionUID = 7328071045193618467L;

	private Integer offset;
	private Integer limit;
	private Map<String, Object> params;

}
