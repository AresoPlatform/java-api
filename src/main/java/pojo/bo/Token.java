

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
public class Token implements Serializable {

	private static final long serialVersionUID = 4043470238789599973L;

	private String token;

	private Date expireTime;

	private Integer userId;

	public Token(String token, Date expireTime) {
		super();
		this.token = token;
		this.expireTime = expireTime;
	}

}
