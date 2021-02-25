
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class WebSocketFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("[WebSocketFilter] [init]");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
        try {
            String[] arr = uri.split("\\/");
            if (arr.length != 4){
                return;
            }
            TokenManager tokenManager = SpringUtil.getBean(TokenManager.class);

            String token = (String) tokenManager.getToken(arr[2]);
            if (StringUtils.isNotEmpty(token)){
                if (!token.equals(arr[3])){
                    return;
                }
            } else {
                return;
            }
            Account account = tokenManager.getAccountByToken(arr[3]);
            if (account == null){
                return;
            }
            if (StringUtils.isNotEmpty(account.getLoginToken()) && !account.getLoginToken().equals(arr[3])){
                return;
            }
            if (account.getId().intValue() != Integer.parseInt(arr[2])){
                return;
            }
        }catch (Exception e){
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("[WebSocketFilter] [destroy]");
    }

}