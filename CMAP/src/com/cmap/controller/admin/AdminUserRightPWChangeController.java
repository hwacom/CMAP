package com.cmap.controller.admin;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.model.UserRightSetting;
import com.cmap.security.SecurityUtil;
import com.cmap.service.UserService;

@Controller
@RequestMapping("/userRightPWChange")
public class AdminUserRightPWChangeController extends BaseController {
	@Log
	private static Logger log;
	
	@Autowired
	UserService userService;

	private void init(Model model, HttpServletRequest request) {
		UserRightSetting user = userService.getUserRightSetting(SecurityUtil.getSecurityUser().getUser().getUserName(), Constants.LOGIN_AUTH_MODE_CM);
		model.addAttribute("userId", user.getId());
		model.addAttribute("userAccount", user.getAccount());
		model.addAttribute("userName", user.getUserName());
		behaviorLog(request);
	}

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String adminEnv(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {
			
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			init(model, request);
		}

		return "admin/admin_user_right_pw_change";
	}
}
