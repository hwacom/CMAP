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
import com.cmap.model.User;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
	@Log
	private static Logger log;
	
	@Autowired
	private HttpServletRequest request;

	//登陸驗證時，通過username獲取使用者的所有權限資訊，
	//並返回User放到spring的全域緩存SecurityContextHolder中，以供授權器使用
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		HttpSession session = request.getSession();
		
		final boolean isAdmin = session.getAttribute(Constants.ISADMIN) != null
									? (boolean)session.getAttribute(Constants.ISADMIN) : false;
		final String passhash = Objects.toString(session.getAttribute(Constants.PASSHASH), "");
		final Object error = session.getAttribute(Constants.ERROR);

		if (error != null && error instanceof ConnectTimeoutException) {
			throw new UsernameNotFoundException("與PRTG連線異常");
		}
		
		if (!isAdmin && StringUtils.isBlank(passhash)) {
			throw new UsernameNotFoundException("帳號或密碼輸入錯誤");
		}
		
		final String role = (String)session.getAttribute(Constants.USERROLE);
		final String[] roles = StringUtils.isNotBlank(role) ? role.split(Env.COMM_SEPARATE_SYMBOL) : null;

		final String userChineseName = Objects.toString(session.getAttribute(Constants.USERNAME), "");
		final String userGroup = Objects.toString(session.getAttribute(Constants.USERGROUP), "");
		final String userUnit = Objects.toString(session.getAttribute(Constants.OIDC_SCHOOL_ID), "");
		final String email = Objects.toString(session.getAttribute(Constants.USEREMAIL), "");
		final String ipAddr = Objects.toString(session.getAttribute(Constants.IP_ADDR), "unknow");
		final String prtgLoginAccount = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");
		final String prtgLoginPassword = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_PASSWORD), "");
		final String oidcSub = Objects.toString(session.getAttribute(Constants.OIDC_SUB), "");
		final String password = Objects.toString(session.getAttribute(Constants.PASSWORD), "");
		
		User user = new User(username, userChineseName, userGroup, userUnit, email, prtgLoginAccount, prtgLoginPassword,
				oidcSub, password, passhash, ipAddr, roles);

		boolean accountEnabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		SecurityUser securityUser = new SecurityUser(
				user,
				Objects.toString(user.getUserChineseName(), username),
				new BCryptPasswordEncoder().encode(user.getPassword()),
				accountEnabled,
				accountNonExpired,
				credentialsNonExpired,
				accountNonLocked,
				getAuthorities(user.getRoles())
				);
		
		session.removeAttribute(Constants.PRTG_LOGIN_PASSWORD);
		session.removeAttribute(Constants.PASSWORD);
		return securityUser;
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
