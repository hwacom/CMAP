package com.cmap.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.utils.impl.CloseableHttpClientUtils;

import sso.core.User;
import sso.web.SingleSignOn;

public class CustomLogoutHandler implements LogoutHandler {
	@Log
	private static Logger log;

	@Autowired
	private SysLoginInfoDAO sysLoginInfoDAO;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		/*
		 ** 呼叫PRTG logout URI進行登出
		 */
		CloseableHttpClient httpclient = CloseableHttpClientUtils.prepare();

		HttpPost httpPost = new HttpPost(Env.PRTG_LOGOUT_URI);

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(Env.HTTP_CONNECTION_TIME_OUT)			//設置連接逾時時間，單位毫秒。
				.setConnectionRequestTimeout(Env.HTTP_CONNECTION_TIME_OUT)	//設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共用連接池的。
				.setSocketTimeout(Env.HTTP_SOCKET_TIME_OUT)					//請求獲取資料的超時時間，單位毫秒。 如果訪問一個介面，多少時間內無法返回資料，就直接放棄此次調用。
				.build();
		httpPost.setConfig(requestConfig);
		
		// For NTPC SSO平台登出(僅有針對NTPC才做)
		if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI)) {
			SingleSignOn sso = new SingleSignOn(request, response);
			if(sso.isAuthenticated()) {
				User user = sso.getUser();
				String schoolId =user.getSchools().getSchools().get(0).getIdentity();
    			String schoolName = user.getSchools().getSchools().get(0).getName();
				sso.logout();				
				//TODO 清除session認證資訊(確認是否所有OIDC都要清?)
				request.getSession().removeAttribute(Constants.OIDC_SUB);
				request.getSession().removeAttribute(Constants.OIDC_SCHOOL_ID);
				request.getSession().removeAttribute(Constants.APACHE_TOMCAT_SESSION_USER_NAME);
				log.info(schoolName+"("+schoolId+") logout from SSO success.");
			}
		}	
		HttpClientContext context = HttpClientContext.create();

		try {
			String sessionId = request.getSession().getId();
			sysLoginInfoDAO.updateLogoutTime(sessionId);
			
			CloseableHttpResponse closeableResponse = httpclient.execute(httpPost, context);

			int statusCode = closeableResponse.getStatusLine().getStatusCode();
			log.info(">>>>>>>>>>>>>>>>>> PRTG logout statusCode: " + statusCode);

			closeableResponse.close();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}
}
