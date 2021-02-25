

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageTableResponse implements Serializable {

	private static final long serialVersionUID = 620421858510718076L;

	private Integer recordsTotal;
	private Integer recordsFiltered;
	private List<?> data;

	private Integer pageCount;

	public PageTableResponse(Integer recordsTotal, Integer recordsFiltered, List<?> data) {
		super();
		this.recordsTotal = recordsTotal;
		this.recordsFiltered = recordsFiltered;
		this.data = data;
	}

}