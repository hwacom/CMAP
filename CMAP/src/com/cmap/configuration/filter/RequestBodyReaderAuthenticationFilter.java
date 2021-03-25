package com.cmap.configuration.filter;

import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

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
import com.cmap.comm.enums.BehaviorType;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.exception.AuthenticateException;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.SysLoginInfo;
import com.cmap.model.UserBehaviorLog;
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

	private void loginAuthByCM(HttpServletRequest request, String username, String password, String loginMode) {
		try {
			final String ipAddr = SecurityUtil.getIpAddr(request);
			request.getSession().setAttribute(Constants.IP_ADDR, ipAddr);
			
			UserRightSetting userRight = userService.getUserRightSetting(username, loginMode);
			
			if(userRight == null) {
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}

			/**
			 * 增加錯誤5次鎖定15分以上
			 * 90天密碼到期鎖定
			 */
			UserBehaviorLog entity = new UserBehaviorLog();
			entity.setLogId(UUID.randomUUID().toString());
			entity.setUserAccount(userRight.getAccount());
			entity.setUserName(userRight.getUserName());
			entity.setTargetPath(request.getRequestURI());
			entity.setDescription(null);
			entity.setBehaviorTime(new Timestamp((new Date()).getTime()));
			
			if (StringUtils.equalsIgnoreCase(userRight.getLoginMode(), Constants.LOGIN_AUTH_MODE_CM) && !StringUtils
					.equals(StringUtils.upperCase(EncryptUtils.getSha256(password)), userRight.getPassword())) {
				log.debug("for debug 1 = " + StringUtils.upperCase(EncryptUtils.getSha256(password))+ 
						", 2 = " + userRight.getPassword() + ", 3 = " + password);	
				
				entity.setBehavior(BehaviorType.LOGIN_FAIL_PW.toString());
				userService.saveOrUpdateEntity(entity);
				
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}
			
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(userRight.getUserGroup());
			
			if(mapping == null) {
				entity.setBehavior(BehaviorType.LOGIN_FAIL_AUTH.toString());
				userService.saveOrUpdateEntity(entity);
				
				throw new ServiceLayerException("使用者登入資訊錯誤!!");
			}
			
			String adminPass = new String(Base64.getDecoder().decode(Env.ADMIN_PASSWORD),Constants.CHARSET_UTF8);
			
			ApiUtils prtgApiUtils = new PrtgApiUtils();
			boolean loginSuccess = prtgApiUtils.login(request, mapping.getPrtgAccount(), adminPass);

			if (loginSuccess) {
				if(!userService.checkPWRetryTimes(userRight.getAccount())) {
					throw new ServiceLayerException("登入錯誤次數超過限制，帳號鎖定"+Env.PASSWORD_VALID_SETTING_LOCK_TIME+"分鐘，請稍後在試!!");
				}
				
				request.getSession().setAttribute(Constants.USERROLE, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
				request.getSession().setAttribute(Constants.ISADMIN, StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?true:false);
				request.getSession().setAttribute(Constants.OIDC_USER_NAME, userRight.getUserName());
				request.getSession().setAttribute(Constants.OIDC_SUB, username);
				request.getSession().setAttribute(Constants.OIDC_SCHOOL_ID, username);
				
	            SysLoginInfo info = new SysLoginInfo();
	            info.setSessionId(request.getSession().getId());
	            info.setIpAddr(ipAddr);
	            info.setAccount(username);
	            info.setUserName(userRight.getUserName());
	            info.setLoginTime(new Timestamp((new Date()).getTime()));
	            sysLoginInfoDAO.saveSysLoginInfo(info);
			}else {
				entity.setBehavior(BehaviorType.LOGIN_FAIL_AUTH.toString());
				userService.saveOrUpdateEntity(entity);
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
			loginAuthByCM(request, username, password, Constants.LOGIN_AUTH_MODE_CM);
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
				UserRightSetting userRight = userService.getUserRightSetting(username, Constants.LOGIN_AUTH_MODE_LDAP);
				
				String userGroup = null;
				if(userRight == null) {			
					// 2021-01-11 Alvin modified for NFU blocking the unknown AD users login by LDAP
					if(StringUtils.isEmpty(Env.LDAP_DEFAULT_PRTG_ACCOUNT)) {
						request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "此帳號無網路管理系統存取權限");
						request.getSession().setAttribute("LDAP_AUTH_RESULT", true);
						throw new ServiceLayerException("LDAP認證成功，但網管系統查無此用戶帳號");
					}else {
						userGroup = new String(Base64.getDecoder().decode(Env.LDAP_DEFAULT_PRTG_ACCOUNT),Constants.CHARSET_UTF8);
					}
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
					request.getSession().setAttribute(Constants.USERROLE, userRight != null && StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?Constants.USERROLE_ADMIN:Constants.USERROLE_USER);
					request.getSession().setAttribute(Constants.ISADMIN, userRight != null && StringUtils.equals(userRight.getIsAdmin(), Constants.DATA_Y)?true:false);
					request.getSession().setAttribute(Constants.OIDC_USER_NAME, userRight != null ?userRight.getUserName():username);
					request.getSession().setAttribute(Constants.OIDC_SUB, username);
					request.getSession().setAttribute(Constants.OIDC_SCHOOL_ID, username);
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
