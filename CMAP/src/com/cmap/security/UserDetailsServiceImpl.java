package com.cmap.security;

import java.util.ArrayList;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.PrtgDAO;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.model.User;
import com.cmap.service.PrtgService;
import com.cmap.service.UserService;
import com.cmap.utils.ApiUtils;
import com.cmap.utils.impl.PrtgApiUtils;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
	@Log
	private static Logger log;
	/*
	@Autowired
	LoginService loginService;
	@Autowired
	SysLogService sysLogService;
	 */

	@Autowired
	private UserService userService;

	@Autowired
	private PrtgDAO prtgDAO;
	
	@Autowired
	private HttpServletRequest request;

	//登陸驗證時，通過username獲取使用者的所有權限資訊，
	//並返回User放到spring的全域緩存SecurityContextHolder中，以供授權器使用
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HttpSession session = request.getSession();
		
		if(StringUtils.isBlank((String)session.getAttribute(Constants.USERROLE))) {
			
			ApiUtils prtgApiUtils = new PrtgApiUtils();
			boolean loginSuccess = false;
			
			boolean checkLDAP = (boolean)request.getSession().getAttribute("LDAP_AUTH_RESULT");
			String sourceId = Objects.toString(request.getSession().getAttribute(Constants.OIDC_SCHOOL_ID), null);
			String password = (String)session.getAttribute(Constants.PASSWORD);
			
			if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_LDAP) && checkLDAP && StringUtils.isNotBlank(sourceId)) {
				PrtgAccountMapping accountMapping = prtgDAO.findPrtgAccountMappingBySourceId(sourceId);
				if(accountMapping == null) {
					 throw new UsernameNotFoundException("帳號或密碼輸入錯誤");
				}
			    username = accountMapping.getPrtgAccount();
			    password = accountMapping.getPrtgPassword();
			}
			
			try {
				loginSuccess = prtgApiUtils.login(request, username, password);				
				
			} catch (Exception e) {
				log.error(e.toString(), e);

	            session.setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "PRTG登入失敗，請重新操作或聯絡系統管理員");
			}
			
			if (!loginSuccess) {
                throw new UsernameNotFoundException("帳號或密碼輸入錯誤");
            }
		}

		final boolean isAdmin = session.getAttribute(Constants.ISADMIN) != null
									? (boolean)session.getAttribute(Constants.ISADMIN) : false;
		final String passhash = (String)session.getAttribute(Constants.PASSHASH);
		final String prtgLoginAccount  = (String)session.getAttribute(Constants.PRTG_LOGIN_ACCOUNT);
		final String prtgLoginPassword  = (String)session.getAttribute(Constants.PRTG_LOGIN_PASSWORD);
		final Object error = session.getAttribute(Constants.ERROR);

		
//		if (StringUtils.equals(Env.LOGIN_AUTH_MODE, Constants.LOGIN_AUTH_MODE_PRTG)) {
			if (error != null && error instanceof ConnectTimeoutException) {
				throw new UsernameNotFoundException("與PRTG連線異常");
			}
			
			if (!chkCanAccessOrNot(request, prtgLoginAccount)) {
				throw new UsernameNotFoundException("帳號或密碼輸入錯誤");
			}
			
			if (!isAdmin && StringUtils.isBlank(passhash)) {
				throw new UsernameNotFoundException("帳號或密碼輸入錯誤");
			}

			
//		}
		
		final String userChineseName = (String)session.getAttribute(Constants.OIDC_USER_NAME);
		final String userUnit = (String)session.getAttribute(Constants.OIDC_SCHOOL_ID);
		final String email = (String)session.getAttribute(Constants.OIDC_EMAIL);
		final String oidcSub = (String)session.getAttribute(Constants.OIDC_SUB);
		final String password = (String)session.getAttribute(Constants.PASSWORD);
		final String ipAddr = (String)session.getAttribute(Constants.IP_ADDR);
		final String role = (String)session.getAttribute(Constants.USERROLE);
		final String schoolId = (String)session.getAttribute(Constants.OIDC_SCHOOL_ID);
		final String[] roles = StringUtils.isNotBlank(role) ? role.split(Env.COMM_SEPARATE_SYMBOL) : null;

		User user = new User(username, userChineseName, userUnit, email, prtgLoginAccount, prtgLoginPassword,
				oidcSub, password, passhash, ipAddr, schoolId, roles);

		boolean accountEnabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		SecurityUser securityUser = new SecurityUser(
				user,
				user.getUserName(),
				new BCryptPasswordEncoder().encode(user.getPassword()),
				//				user.getPassword(),
				accountEnabled,
				accountNonExpired,
				credentialsNonExpired,
				accountNonLocked,
				getAuthorities(user.getRoles())
				);
		//		sysLogService.saveSysLog(new SysLog(user, SysLogService.LOGIN));

		// 清除密碼資訊
		session.removeAttribute(Constants.PRTG_LOGIN_PASSWORD);
		session.removeAttribute(Constants.PASSWORD);
		return securityUser;
	}

	private boolean chkCanAccessOrNot(HttpServletRequest request, String account) {
        return userService.checkUserCanAccess(request, true, Constants.DATA_STAR_SYMBOL, null, account);
	}

	public ArrayList<GrantedAuthority> getAuthorities(String... roles) {
		ArrayList<GrantedAuthority> authorities = new ArrayList<>(roles.length);
		for (String role : roles) {
			Assert.isTrue(!role.startsWith("ROLE_"), role + " cannot start with ROLE_ (it is automatically added)");
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
		}
		return authorities;
	}
}
