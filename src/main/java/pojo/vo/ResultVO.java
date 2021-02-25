
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Data
public class ResultVO {

    private Integer code;   

    private String msg;

    private Object data;

    private Integer totalCount;   

    private Integer pageSize;   

    private Integer pageCount;   


    public ResultVO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultVO(Object data) {
        this.code = ResultCode.Success.getCode();
        this.data = data;
    }

    public ResultVO(Object data, Integer totalCount, Integer pageSize, Integer pageCount) {
        this.code = ResultCode.Success.getCode();
        this.data = data;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.pageCount = pageCount;
    }

    public static ResultVO returnMsg(Integer code, String msg){
        return new ResultVO(code, msg);
    }

    public static ResultVO returnMsg(ResultCode resultCode){
        return new ResultVO(resultCode.getCode(), resultCode.getMsg());
    }

    public static ResultVO returnSingleData(Object data) {
        return new ResultVO(data);
    }

    public static ResultVO returnPagingData(Object data, Integer totalCount, Integer pageSize, Integer pageCount) {
        return new ResultVO(data, totalCount, pageSize, pageCount);
    }

    public static void returnMsg(HttpServletResponse response, Integer code, String msg) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter pw = response.getWriter();
        ResultVO result = new ResultVO(code, msg);
        String resultJson = JSONObject.toJSONString(result);
        pw.write(resultJson);
    }

}