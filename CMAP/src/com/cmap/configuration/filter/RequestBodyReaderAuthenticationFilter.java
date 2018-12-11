package com.cmap.configuration.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.BaseAuthentication;
import com.cmap.exception.AuthenticateException;
import com.cmap.security.SecurityUtil;
import com.cmap.utils.ApiUtils;
import com.cmap.utils.impl.PrtgApiUtils;

public class RequestBodyReaderAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	@Log
	private static Logger log;

	public RequestBodyReaderAuthenticationFilter() {
	}

	/**
	 * 解析request form資訊取得使用者輸入的帳號&密碼
	 * @param requestBody
	 * @return
	 */
	/*
    private Map<String, String> composeUserNamePasswordMap(String requestBody) {
    	Map<String, String> retMap = new HashMap<String, String>();

    	//username=prtgadmin&password=prtgadmin
    	if (requestBody != null && requestBody.indexOf("&") != -1) {
    		String[] temp = requestBody.split("&");
        	for (String t : temp) {
        		String key = t.split("=")[0];
        		String value = t.split("=")[1];
        		retMap.put(key, value);
        	}
    	}

    	return retMap;
    }
	 */

	private void loginAuthByPRTG(HttpServletRequest request, String username, String password) {
		try {
			final String ipAddr = SecurityUtil.getIpAddr(request);
			request.getSession().setAttribute(Constants.IP_ADDR, ipAddr);

			ApiUtils prtgApiUtils = new PrtgApiUtils();
			boolean loginSuccess = prtgApiUtils.login(request, username, password);

			if (loginSuccess) {
				request.getSession().setAttribute(Constants.USERROLE, Constants.USERROLE_USER);
			}

		} catch (AuthenticateException ae) {
			log.error(ae.toString());

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		BaseAuthentication.authAdminUser(request, username, password);
		BaseAuthentication.authAdminRole(request, username);
	}

	/**
	 * 攔截登入表單，進行PRTG驗證
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		//        String requestBody;
		//            requestBody = IOUtils.toString(request.getReader());

		//每次登入動作首先清空Session所有值
		request.getSession().invalidate();

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		request.getSession().setAttribute(Constants.USERNAME, username);
		request.getSession().setAttribute(Constants.PASSWORD, password);

		//            Map<String, String> authMap = composeUserNamePasswordMap(requestBody);

		if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_PRTG)) {
			loginAuthByPRTG(request, username, password);
		}

		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, token);

		return this.getAuthenticationManager().authenticate(token);
	}
}