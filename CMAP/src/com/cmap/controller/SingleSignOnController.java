package com.cmap.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cmap.Constants;
import com.cmap.annotation.Log;

import sso.core.School;
import sso.core.User;
import sso.web.SingleSignOn;

/**
 * 外不平台登入驗證流程
 * 目前 for 新北教網
 * @author 狂暴小雞
 *
 */
@Controller
@RequestMapping("/auth")
public class SingleSignOnController extends BaseController {
    @Log
    private static Logger log;
    /**
     * Local Constant
     */
    final String ROLE_LIMIT_NTPC = "資訊組長"; // 登入者身分限制 for NTPC
    
    /**
     * 驗證身分登入
     * @param request
     * @param response
     * @return the URL of redirection
     */
    @RequestMapping(value = {"/doSSO","/callback"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String doSingleSignOn(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
    	try {
    		SingleSignOn sso = new SingleSignOn(request, response);
    		if (!sso.isAuthenticated()) {
    			log.debug("is not authenticated, sso.login()");
    			String returnUrl=request.getRequestURL().toString();
    			sso.setReturnUrl(returnUrl);
    			sso.login();
    			log.debug("sso.login() done and redirect");
    			//sso.login已包含導頁所以不可以再次導頁會觸發重複導頁例外
    			return null;
    		} else {
    			log.debug("is authenticated, sso.getUser()");
    			User user = sso.getUser();
    			if(user == null) {
    				request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "無網路管理系統存取權限，請與系統管理員聯繫");
    				return "redirect:/loginOIDC_NTPC";
    			}
    			// 迴圈檢索帳號所屬學校資料 挑出符合資訊組長的項目建立acctInfoList清單
    			List<School> schools = user.getSchools().getSchools(); // 該帳號使用者所屬學校清單
    			List<Map<String, String>> acctInfoList = new ArrayList<Map<String, String>>();
    			for(School school : schools) {
    				if(school.getGroups().contains(ROLE_LIMIT_NTPC)) {
    					log.info("User contains the role [ " + ROLE_LIMIT_NTPC + " ] in " + school.getName());
    					log.info("GroupList : " + school.getGroups().toString());
    	    			HashMap<String, String> acctInfo = new HashMap<String, String>();
    	    			acctInfo.put("schoolId", school.getIdentity());
    	    			acctInfo.put("schoolName", school.getName());
    	    			acctInfoList.add(acctInfo); // 跨校多重身分時會一併添加進acctInfoList清單(供選擇器抓取)
    				}else {
    					continue;
    				}
    			}
				String schoolId = "";
				String schoolName = "";
    			if(acctInfoList.size()==1) {
    				schoolId = acctInfoList.get(0).get("schoolId");
    				schoolName = acctInfoList.get(0).get("schoolName");
        			request.getSession().setAttribute(Constants.USERACCOUNT, schoolId);
        			request.getSession().setAttribute(Constants.OIDC_SCHOOL_ID, schoolId);
        			request.getSession().setAttribute(Constants.USERNAME, schoolName);
        			request.getSession().setAttribute(Constants.APACHE_TOMCAT_SESSION_USER_NAME, schoolName);
        			log.info(schoolName+"("+schoolId+") auth by SSO success.");
    			}else if(acctInfoList.size()>1) {
        			//TODO 新增選擇器流程及UI
    				schoolId = acctInfoList.get(0).get("schoolId");
    				schoolName = acctInfoList.get(0).get("schoolName");
        			request.getSession().setAttribute(Constants.USERACCOUNT, schoolId);
        			request.getSession().setAttribute(Constants.OIDC_SCHOOL_ID, schoolId);
        			request.getSession().setAttribute(Constants.USERNAME, schoolName);
        			request.getSession().setAttribute(Constants.APACHE_TOMCAT_SESSION_USER_NAME, schoolName);
        			//TODO 新增選擇器流程及UI
        			log.info(schoolName+"("+schoolId+") auth by SSO success.");
    			}else {
    				request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "此帳號無網路管理系統存取權限，請與系統管理員聯繫");
    				sso.logout();
    				log.info("SSO帳號查無符合限制身分資格 [ " + ROLE_LIMIT_NTPC + " ] 登入失敗(自動登出SSO)");
    				return "redirect:/loginOIDC_NTPC";
    			}

    			try {
    				boolean canAccess = checkUserCanOrNotAccess(request, schoolId, Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI, null);
        			if (canAccess) {
        				return loginAuthByPRTG(model, principal, request, schoolId, Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI);
        			} else {
        				request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "此帳號無網路管理系統存取權限，請與系統管理員聯繫");
        				return "redirect:/loginOIDC_NTPC";
        			}    
    			}catch(Exception e) {
    	    		request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "SSO帳號登入資訊驗證失敗，請與系統管理員聯繫");
    	            log.error(e.toString(), e);
    	            return "redirect:/loginOIDC_NTPC";	
    			}
    		}
    	}catch (Exception e) {
    		request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "SSO平台連線存取資料異常，請與系統管理員聯繫");
            log.error(e.toString(), e);
            return "redirect:/loginOIDC_NTPC";
        } finally {
        	behaviorLog(request);
        }
    }
}