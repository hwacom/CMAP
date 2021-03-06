package com.cmap.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.model.UserRightSetting;
import com.cmap.service.UserService;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;

@Controller
@RequestMapping("/")
public class LoginContoller extends BaseController {
	@Log
	private static Logger log;
	
	@Autowired
	UserService userService;
	
	private String chkLoginPage(HttpServletRequest request) {
	    HttpSession session = request.getSession();	   
		String uri = "";
		
	    if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_MIAOLI)) {
	    	uri = "redirect:/loginOIDC";

        } else if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI)) {
//            String preUrl = ObjectUtils.toString(session.getAttribute(Constants.PREVIOUS_URL), null);
//            
//            if (StringUtils.isBlank(preUrl) || StringUtils.equals(preUrl, "/") || StringUtils.equals(preUrl, "/login")) {
//            	
//            	return "redirect:/loginOIDC_NTPC";
//
//            } else {
//                return "redirect:" + preUrl;
//            }
        	uri = "redirect:/loginOIDC_NTPC";

        } else if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_CHIAYI)) {
        	uri = "redirect:/loginOIDC_CY";
            
        }else {
        	uri = "redirect:/login";
        }
	    
	    log.info("redirect uri = " + uri);
	    return uri;
	}

	/**
	 ** 判斷要導到哪種登入頁面
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public String check(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
	        HttpSession session = request.getSession();
            if (session != null) {
                DefaultSavedRequest dsr = (DefaultSavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                if (dsr != null) {
                    String servletPath = dsr.getServletPath();
                    session.setAttribute(Constants.PREVIOUS_URL, servletPath);
                }
            }

			if (null == principal) {
				return chkLoginPage(request);
			}

			String checkpage =  checkPWDate(request);
			return checkpage != null ? checkpage : "redirect:" + Env.HOME_PAGE;

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (null == principal) {
			    return chkLoginPage(request);
			}

			String checkpage =  checkPWDate(request);
			return checkpage != null ? checkpage : "redirect:" + Env.HOME_PAGE;

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}

	@RequestMapping(value = "index", method = RequestMethod.GET)
	public String indexPage(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (null == principal) {
			    return chkLoginPage(request);
			}

			String previousPage = Objects.toString(request.getSession().getAttribute(Constants.PREVIOUS_URL));

			String redirectUrl = StringUtils.isNotBlank(previousPage) && StringUtils.contains(previousPage, "/plugin/module/vmswitch/power/off")
			                        ? previousPage : Env.HOME_PAGE;
			
			String checkpage =  checkPWDate(request);
			return checkpage != null ? checkpage : "redirect:" + redirectUrl;

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}
	
	private String checkPWDate(HttpServletRequest request) {

		try {
			if (StringUtils.equalsIgnoreCase(Constants.DATA_Y ,(Env.HIGH_AVAILABILITY_FLAG)) 
					&& !StringUtils.equals(InetAddress.getLocalHost().getHostAddress(), Env.HIGH_AVAILABILITY_ALIVE_SERVER_IP)) {
				request.getSession().setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "請使用Primary Server入口進入系統!!");
				return "redirect:/login";
			}
		} catch (UnknownHostException e2) {
		}
		
		if(StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_FLAG)
				&& !StringUtils.isBlank(Env.PASSWORD_VALID_SETTING_VALIDITY_PERIOD)) {
			
			Date checkDate = DateUtils.addDays(new Date(), -(Integer.parseInt(Env.PASSWORD_VALID_SETTING_VALIDITY_PERIOD)));
			UserRightSetting userRight = userService.getUserRightSetting(request.getSession().getAttribute(Constants.USERACCOUNT).toString(), Constants.LOGIN_AUTH_MODE_CM);
			if(userRight != null && userRight.getLastPWUpdateTime().compareTo(checkDate) < 0) {
				request.getSession().setAttribute("checkPWDate", false);
				return "redirect:/userRightPWChange/main";
			}
		}
		request.getSession().setAttribute("checkPWDate", true);
		return null;
	}
	
	@RequestMapping(value = "login/app", method = RequestMethod.GET)
    public String loginForApp(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            HttpSession session = request.getSession();
            session.setAttribute(Constants.LOGIN_FROM_APP, Constants.DATA_Y);
            session.removeAttribute(Constants.PREVIOUS_URL);
            return "redirect:/loginOIDC_NTPC";  //TODO:先寫死 for APP測試用

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return null;
    }

	@RequestMapping(value = "login/returnApp", method = RequestMethod.GET)
    public @ResponseBody AppResponse loginReturnApp(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

	    HttpSession session = request.getSession();
	    String username = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_ACCOUNT));
	    String passhash = Objects.toString(session.getAttribute(Constants.PASSHASH));

        AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "Success");
        app.putData(Constants.USERNAME, username);
        app.putData(Constants.PASSHASH, passhash);
        return app;
    }

	@RequestMapping(value = "login", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginPage(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "langType", defaultValue = "zh_TW") String langType,
			Locale locale,
			Principal principal,
			Model model) {

		HttpSession session = request.getSession();
		LocaleContextHolder.getLocale();

		final String loginError = Objects.toString(session.getAttribute(Constants.MODEL_ATTR_LOGIN_ERROR), null);
		if (StringUtils.isNotBlank(loginError)) {
			model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, loginError);
			session.removeAttribute(Constants.MODEL_ATTR_LOGIN_ERROR);
			// 2021-01-13 Alvin modified 登入發生錯誤有錯誤訊息時不需chkLoginPage,否則會因為redirect頁面行為重置request物件洗空錯誤訊息
			return "login";

		} else {
			if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_MIAOLI)) {
				URI configurationEndpoint = null;
				try {
					configurationEndpoint = new URI(Env.OIDC_CONFIGURATION_ENDPOINT);

				} catch (URISyntaxException e) {
					log.error(e.toString(), e);

					try {
						configurationEndpoint = new URI(Constants.OIDC_MLC_CONFIGURATION_ENDPOINT);

					} catch (URISyntaxException e1) {
						log.error(e1.toString(), e1);
					}
				}
				request.getSession().setAttribute(Constants.OIDC_CONFIGURATION_ENDPOINT, configurationEndpoint.toString());

				return "redirect:/loginOIDC";

			}  else if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_CHIAYI)) {
//				URI configurationEndpoint = null;
//				try {
//					configurationEndpoint = new URI(Env.OIDC_CONFIGURATION_ENDPOINT);
//
//				} catch (URISyntaxException e) {
//					log.error(e.toString(), e);
//
//					try {
//						configurationEndpoint = new URI(Constants.OIDC_CY_CONFIGURATION_ENDPOINT);
//
//					} catch (URISyntaxException e1) {
//						log.error(e1.toString(), e1);
//					}
//				}
//				request.getSession().setAttribute(Constants.OIDC_CONFIGURATION_ENDPOINT, configurationEndpoint.toString());
//
//				return "redirect:/loginOIDC_CY";

				return "login";
			}  else if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI)) {

//				return "redirect:/loginOIDC_NTPC";
				return "login";
				
			} else {
		        return "login";
			}
		}
	}

	@RequestMapping(value = "loginOIDC", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginOIDCPage(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "langType", defaultValue = "en_US") String langType,
			Locale locale,
			Principal principal,
			Model model) {

		HttpSession session = request.getSession();
		LocaleContextHolder.getLocale();

		final String loginError = Objects.toString(session.getAttribute(Constants.MODEL_ATTR_LOGIN_ERROR), null);
		if (StringUtils.isNotBlank(loginError)) {
			model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, loginError);
			session.removeAttribute(Constants.MODEL_ATTR_LOGIN_ERROR);

			if (!Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_MIAOLI)) {
	        	return chkLoginPage(request);
	        }
	        
	        return "login_openid_mlc";

		} else {
			if (Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_MIAOLI)) {
				URI configurationEndpoint = null;
				try {
					configurationEndpoint = new URI(Env.OIDC_CONFIGURATION_ENDPOINT);

				} catch (URISyntaxException e) {
					log.error(e.toString(), e);

					try {
						configurationEndpoint = new URI(Constants.OIDC_MLC_CONFIGURATION_ENDPOINT);

					} catch (URISyntaxException e1) {
						log.error(e1.toString(), e1);
					}
				}
				request.getSession().setAttribute(Constants.OIDC_CONFIGURATION_ENDPOINT, configurationEndpoint.toString());

				log.info("login with login_openid_mlc");
				return "login_openid_mlc";
			}else {
				return chkLoginPage(request);
			}			
		}
	}
	
	@RequestMapping(value = "loginOIDC_NTPC", method = {RequestMethod.GET, RequestMethod.POST})
    public String loginOIDC_NTPC_Page(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "langType", defaultValue = "en_US") String langType,
            Locale locale,
            Principal principal,
            Model model) {

        HttpSession session = request.getSession();
        LocaleContextHolder.getLocale();

        final String loginError = Objects.toString(session.getAttribute(Constants.MODEL_ATTR_LOGIN_ERROR), null);
        model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, loginError);
        session.removeAttribute(Constants.MODEL_ATTR_LOGIN_ERROR);
        
        if (!Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_NEW_TAIPEI)) {
        	return chkLoginPage(request);
        }
        
        return "login_openid_ntpc";
    }

	@RequestMapping(value = "loginOIDC_CY", method = {RequestMethod.GET, RequestMethod.POST})
	public String loginOIDC_CY_Page(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "langType", defaultValue = "en_US") String langType,
			Locale locale,
			Principal principal,
			Model model) {

		HttpSession session = request.getSession();
		LocaleContextHolder.getLocale();

		final String loginError = Objects.toString(session.getAttribute(Constants.MODEL_ATTR_LOGIN_ERROR), null);
		model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, loginError);
		session.removeAttribute(Constants.MODEL_ATTR_LOGIN_ERROR);
		
		if (!Env.LOGIN_AUTH_MODE.equals(Constants.LOGIN_AUTH_MODE_OIDC_CHIAYI)) {
        	return chkLoginPage(request);
        }
        
        return "login_openid_cy";
	}
	
	@RequestMapping(value = "login/authByOIDC", method = {RequestMethod.GET, RequestMethod.POST})
	public String authByOIDC(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		ClientID clientID = null;
	    Secret clientSecret = null;
	    URI tokenEndpoint = null;
	    URI authEndpoint = null;
	    String jwksURI = null;
	    URI userinfoEndpointURL = null;
	    URI eduinfoEndpointURL = null;
        String redirectURI = null;

		HttpSession session = request.getSession();
        String login = request.getParameter("login");
        if (login == null) {
            login = "mlc";  //預設苗栗縣教育雲帳號服務登入
        }
        
        try {

            if (login.equals("google")) {

            } else {
                //mlc clientid
                clientID = new ClientID(Env.OIDC_CLIENT_ID);
                clientSecret = new Secret(Env.OIDC_CIENT_SECRET);
                authEndpoint = new URI(Env.OIDC_AUTH_ENDPOINT);
                tokenEndpoint = new URI(Env.OIDC_TOKEN_ENDPOINT);
                userinfoEndpointURL = new URI(Env.OIDC_USER_INFO_ENDPOINT);
                eduinfoEndpointURL = new URI(Env.OIDC_EDU_INFO_ENDPOINT);
                jwksURI = Env.OIDC_JWKS_URI;
                redirectURI = Env.OIDC_REDIRECT_URI;
            }

            session.setAttribute(Constants.OIDC_CLIENT_ID, clientID.getValue());
            session.setAttribute(Constants.OIDC_CLIENT_SECRET, clientSecret.getValue());
            session.setAttribute(Constants.OIDC_TOKEN_ENDPOINT, tokenEndpoint.toString());
            session.setAttribute(Constants.OIDC_USER_INFO_ENDPOINT, userinfoEndpointURL.toString());
            session.setAttribute(Constants.OIDC_EDU_INFO_ENDPOINT, eduinfoEndpointURL.toString());
            session.setAttribute(Constants.OIDC_JWKS_URI, jwksURI);

            URI callback = new URI(redirectURI);
            session.setAttribute(Constants.OIDC_REDIRECT_URI, redirectURI);

            // Generate random state string for pairing the response to the request
            State state = new State();
            session.setAttribute(Constants.OIDC_STATE, state.toString());

            // Generate nonce
            Nonce nonce = new Nonce();

            // Compose the request (in code flow)
            AuthenticationRequest authzReq = new AuthenticationRequest(
                    authEndpoint,
                    new ResponseType(Env.OIDC_RESPONSE_TYPE),
                    Scope.parse(Env.OIDC_SCOPE),
                    clientID,
                    callback,
                    state,
                    nonce);

            log.info("1.User authorization request");
            log.info(authzReq.getEndpointURI().toString() + "?" + authzReq.toQueryString());
            try {
				response.sendRedirect(authzReq.getEndpointURI().toString() + "?" + authzReq.toQueryString());

			} catch (IOException ioe) {
				log.error(ioe.toString(), ioe);

				model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "連接苗栗縣教育雲端帳號認證服務失敗，請重新操作或聯絡系統管理員");
				return "login_openid_mlc";
			}

        } catch (URISyntaxException ex) {
        	log.error(ex.toString(), ex);

        	model.addAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "OIDC授權驗證流程發生問題，請重新操作");
			return "login_openid_mlc";
        }

		return null;
	}
	
}
