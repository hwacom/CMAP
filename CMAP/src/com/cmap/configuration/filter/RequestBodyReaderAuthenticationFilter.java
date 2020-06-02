package com.cmap.configuration.filter;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.exception.AuthenticateException;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.SysLoginInfo;
import com.cmap.model.UserRightSetting;
import com.cmap.security.SecurityUtil;
import com.cmap.service.PrtgService;
import com.cmap.service.UserService;
import com.cmap.utils.ApiUtils;
import com.cmap.utils.impl.EncryptUtils;
import com.cmap.utils.impl.LDAPUtils;
import com.cmap.utils.impl.PrtgApiUtils;

public class RequestBodyReaderAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	@Log
	private static Logger log;

	@Autowired
	private UserService userService;
	
	@Autowired
	private PrtgService prtgService;
	
	@Autowired
	private SysLoginInfoDAO sysLoginInfoDAO;
	
	public RequestBodyReaderAuthenticationFilter() {
	}

	private void loginAuthByCM(HttpServletRequest request, String username, String password) {
		try {
			final String ipAddr = SecurityUtil.getIpAddr(request);
			request.getSession().setAttribute(Constants.IP_ADDR, ipAddr);
			
			UserRightSetting userRight = userService.getUserRightSetting(username);
			
			if(userRight == null) {
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}else if(!StringUtils.equals(StringUtils.upperCase(EncryptUtils.getSha256(password)), userRight.getPassword())){
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}
			
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(userRight.getUserGroup());
			
			if(mapping == null) {
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}
			
			String adminPass = new String(Base64.getDecoder().decode(Env.ADMIN_PASSWORD),Constants.CHARSET_UTF8);
			
			ApiUtils prtgApiUtils = new PrtgApiUtils();
			boolean loginSuccess = prtgApiUtils.login(request, mapping.getPrtgAccount(), adminPass);

			if (loginSuccess) {
				request.getSession().setAttribute(Constants.USERROLE, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
				request.getSession().setAttribute(Constants.ISADMIN, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?true:false);
				request.getSession().setAttribute(Constants.OIDC_USER_NAME, userRight.getUserName());
				
	            SysLoginInfo info = new SysLoginInfo();
	            info.setSessionId(request.getSession().getId());
	            info.setIpAddr(ipAddr);
	            info.setAccount(username);
	            info.setUserName(userRight.getUserName());
	            info.setLoginTime(new Timestamp((new Date()).getTime()));
	            sysLoginInfoDAO.saveSysLoginInfo(info);
			}

		} catch (AuthenticateException ae) {
			log.error(ae.toString());

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 攔截登入表單，進行PRTG驗證
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
	    String previousPage = Objects.toString(request.getSession().getAttribute(Constants.PREVIOUS_URL));

		//每次登入動作首先清空Session所有值
		request.getSession().invalidate();

		if (StringUtils.isNotBlank(previousPage)) {
		    request.getSession().setAttribute(Constants.PREVIOUS_URL, previousPage);
		    log.info("for debug previousPage ==== "+previousPage);
		}

		String username = obtainUsername(request);
		String password = obtainPassword(request);

		request.getSession().setAttribute(Constants.USERNAME, username);
		request.getSession().setAttribute(Constants.PASSWORD, password);
		request.getSession().setAttribute(Constants.APACHE_TOMCAT_SESSION_USER_NAME, username);

		if(Env.LOGIN_MODE.contains(Constants.LOGIN_AUTH_MODE_LDAP) && Env.LDAP_URL != null && Env.LDAP_DOMAIN != null) {
			loginAuthByLDAP(request, username, password);
		}
		
		boolean checkLDAP = Boolean.TRUE == request.getSession().getAttribute("LDAP_AUTH_RESULT");
		if(!checkLDAP) {
			loginAuthByCM(request, username, password);
		}		
		
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

		// Allow subclasses to set the "details" property
		setDetails(request, token);

		return this.getAuthenticationManager().authenticate(token);
	}
	
	private void loginAuthByLDAP(HttpServletRequest request, String username, String password) {
		try {
			
			final String ipAddr = SecurityUtil.getIpAddr(request);
			request.getSession().setAttribute(Constants.IP_ADDR, ipAddr);
			request.getSession().setAttribute("LDAP_AUTH_RESULT", false);
			
			LDAPUtils utils = new LDAPUtils();
			boolean loginSuccess = utils.LDAP_AUTH_AD(request, username, password);

			if (loginSuccess) {
				UserRightSetting userRight = userService.getUserRightSetting(username);
				
				String userGroup = null;
				if(userRight == null) {
					userGroup = new String(Base64.getDecoder().decode(Env.LDAP_DEFAULT_PRTG_ACCOUNT),Constants.CHARSET_UTF8);
				}else {
					userGroup = userRight.getUserGroup();
				}
				
				PrtgAccountMapping mapping = prtgService.getMappingByAccount(userGroup);
				
				if(mapping == null) {
					throw new ServiceLayerException("使用者登入資訊錯誤!!");
				}
				
				String adminPass = new String(Base64.getDecoder().decode(Env.ADMIN_PASSWORD),Constants.CHARSET_UTF8);
				
				ApiUtils prtgApiUtils = new PrtgApiUtils();
				loginSuccess = prtgApiUtils.login(request, mapping.getPrtgAccount(), adminPass);

				if (loginSuccess) {
					request.getSession().setAttribute(Constants.USERROLE, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
					request.getSession().setAttribute(Constants.ISADMIN, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?true:false);
					request.getSession().setAttribute(Constants.OIDC_USER_NAME, username);
					request.getSession().setAttribute("LDAP_AUTH_RESULT", true);
					
		            SysLoginInfo info = new SysLoginInfo();
		            info.setSessionId(request.getSession().getId());
		            info.setIpAddr(ipAddr);
		            info.setAccount(username);
		            info.setUserName(username);
		            info.setLoginTime(new Timestamp((new Date()).getTime()));
		            sysLoginInfoDAO.saveSysLoginInfo(info);
				}
			}

		} catch (AuthenticateException ae) {
			log.error(ae.toString());

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		
	}
}
