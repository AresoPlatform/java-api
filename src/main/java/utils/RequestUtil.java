
import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RequestUtil {

	public static void printResult (HttpServletRequest request, HttpServletResponse response, ResultVO msg) throws IOException {
		PrintWriter writer = null;
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");

		String origin = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Origin", origin);
		response.setHeader("Access-Control-Allow-Methods", "*");
		response.setHeader("Access-Control-Allow-Headers","Origin,Content-Type,Accept,token,X-Requested-With");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		try {
			writer = response.getWriter();
			writer.print(JSONObject.toJSONString(msg));
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if(writer != null){
				writer.close();
			}
		}
	}
}
