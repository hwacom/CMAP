package com.cmap.controller;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.cmap.AppResponse;
import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.comm.enums.BehaviorType;
import com.cmap.dao.SysLoginInfoDAO;
import com.cmap.exception.AuthenticateException;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.SysLoginInfo;
import com.cmap.model.User;
import com.cmap.model.UserBehaviorLog;
import com.cmap.model.UserRightSetting;
import com.cmap.security.SecurityUser;
import com.cmap.security.SecurityUtil;
import com.cmap.service.CommonService;
import com.cmap.service.UserService;
import com.cmap.utils.ApiUtils;
import com.cmap.utils.impl.CloseableHttpClientUtils;
import com.cmap.utils.impl.PrtgApiUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


@Controller
@RequestMapping("/base")
public class BaseController {
	private static final Logger log = LoggerFactory.getLogger(BaseController.class);

	SimpleDateFormat sdfYearMonth = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	@Autowired
	protected CommonService commonService;

	@Autowired
	private UserService userService;

	@Autowired
	private SysLoginInfoDAO sysLoginInfoDAO;
	
	public BaseController() {
		super();
		sdfYearMonth.setTimeZone(TimeZone.getTimeZone("GMT"));
		sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
		sdfDateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	protected Object transJSON2Object(String jsonStr, Class<?> mClass) {
		ObjectMapper oMapper = new ObjectMapper();
		try {
			return oMapper.readValue(jsonStr, mClass);

		} catch (Exception e) {
			return null;
		}
	}

	protected String loginAuthByPRTG(Model model, Principal principal, HttpServletRequest request, String userName, String loginMode) {
        HttpSession session = request.getSession();
        Map<String, String> prtgMap = null;
        
        try {
        	UserRightSetting userRight = userService.getUserRightSetting(userName, loginMode);
        	
        	if(userRight == null) {
				throw new AuthenticateException("PRTG登入失敗 >> 取不到 使用者登入資訊 userName: " + userName + " )");
			}
			        	
        	prtgMap = commonService.findUserGroupList(userRight.getUserGroup());

			if (prtgMap == null || prtgMap.isEmpty()) {
				throw new AuthenticateException("PRTG登入失敗 >> 取不到 Prtg_Account_Mapping 資料 (username: " + userRight.getUserGroup() + " )");
			}

            String adminPass = new String(Base64.getDecoder().decode(Env.ADMIN_PASSWORD),Constants.CHARSET_UTF8);
            
            ApiUtils prtgApiUtils = new PrtgApiUtils();
            boolean loginSuccess = prtgApiUtils.login(request, prtgMap.get(userRight.getUserGroup()), adminPass);

            if (!loginSuccess) {
                throw new AuthenticateException("PRTG登入失敗 >> prtgApiUtils.login return false");
            }

            final String ipAddr = SecurityUtil.getIpAddr(request);
            
            SysLoginInfo info = new SysLoginInfo();
            info.setSessionId(request.getSession().getId());
            info.setIpAddr(ipAddr);
            info.setAccount(userRight.getAccount());
            info.setUserName(userRight.getUserName());
            info.setLoginTime(new Timestamp((new Date()).getTime()));
            sysLoginInfoDAO.saveSysLoginInfo(info);
            
			session.setAttribute(Constants.USERGROUP, prtgMap.get(userRight.getUserGroup()));
			request.getSession().setAttribute(Constants.USEREMAIL, Objects.toString(userRight.getEmail(), ""));
			
            String role = Objects.toString(session.getAttribute(Constants.USERROLE), null);

            if (StringUtils.isBlank(role)) {
                request.getSession().setAttribute(Constants.USERROLE, Constants.USERROLE_USER);

            } else {
                if (role.indexOf(Constants.USERROLE_USER) == -1) {
                    role = role.concat(Env.COMM_SEPARATE_SYMBOL).concat(Constants.USERROLE_USER);
                    request.getSession().setAttribute(Constants.USERROLE, role);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);

            session.setAttribute(Constants.MODEL_ATTR_LOGIN_ERROR, "PRTG登入失敗，請重新操作或聯絡系統管理員");
            return "redirect:/login";
        }

        return manualAuthenticatd4EduOIDC(model, principal, request);
    }

	protected String manualAuthenticatd4EduOIDC(Model model, Principal principal, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();

			String userRole = Objects.toString(session.getAttribute(Constants.USERROLE), "");
			String[] userRoles = StringUtils.split(userRole, Env.COMM_SEPARATE_SYMBOL);
			final String[] USER_ROLES = userRoles;

			final String USER_ACCOUNT = Objects.toString(session.getAttribute(Constants.USERACCOUNT), "");
			final String USER_NAME = Objects.toString(session.getAttribute(Constants.USERNAME), "");
			final String USER_GROUP = Objects.toString(session.getAttribute(Constants.USERGROUP), "");
			final String USER_UNIT = Objects.toString(session.getAttribute(Constants.OIDC_SCHOOL_ID), "");
			final String USER_EMAIL = Objects.toString(session.getAttribute(Constants.USEREMAIL), "");
			final String USER_IP = Objects.toString(session.getAttribute(Constants.IP_ADDR), "unknow");
			final String PRTG_ACCOUNT = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");
			final String PRTG_PASSWORD = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_PASSWORD), "");
			final String PRTG_PASSHASH = Objects.toString(session.getAttribute(Constants.PASSHASH), "");
			final String OIDC_SUB = Objects.toString(session.getAttribute(Constants.OIDC_SUB), "");
			final String PASSWORD = Objects.toString(session.getAttribute(Constants.PASSWORD), "");

			List<SimpleGrantedAuthority> authorities = new ArrayList<>();

			for (String ROLE : USER_ROLES) {
				authorities.add(new SimpleGrantedAuthority("ROLE_" + ROLE));
			}

			User user = new User(
								USER_ACCOUNT,
								USER_NAME,
								USER_GROUP,
								USER_UNIT,
								USER_EMAIL,
								PRTG_ACCOUNT,
								PRTG_PASSWORD,
								OIDC_SUB,
								PASSWORD,
								PRTG_PASSHASH,
								USER_IP,
								USER_ROLES);
			SecurityUser securityUser = new SecurityUser(user, USER_NAME, PASSWORD, true, true, true, true, authorities);

	        Authentication authentication =  new UsernamePasswordAuthenticationToken(securityUser, USER_NAME, authorities);
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        final String loginFromApp = Objects.toString(session.getAttribute(Constants.LOGIN_FROM_APP), Constants.DATA_N);

	        // 清除密碼資訊
	        session.removeAttribute(Constants.PRTG_LOGIN_PASSWORD);

	        if (StringUtils.equals(loginFromApp, Constants.DATA_Y)) {
	            return "redirect:/login/returnApp";

	        } else {
	            if (Env.ENABLE_PRTG_SSH_CONFIRM_PAGE) {
	                return "redirect:" + Env.PRTG_SSH_CONFIRM_PAGE;
	            } else {
	                return "redirect:" + Env.HOME_PAGE;
	            }
	        }

		} catch (Exception e) {
			log.error(e.toString(), e);
			return "redirect:/login";
		}
	}

	protected boolean checkUserCanOrNotAccess(HttpServletRequest request, String account, String loginMode, String[] roles) {
		return userService.checkUserCanAccess(request, account, loginMode, roles);
	}

	protected void setQueryGroupList(HttpServletRequest request, Object obj, String fieldName, String queryGroup) throws Exception {
		if (StringUtils.isBlank(queryGroup)) {
			//如果未選擇特定群組，則須依照使用者權限，給予可查詢的群組清單
		    String prtgAccount = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

            if (StringUtils.isBlank(prtgAccount)) {
                throw new ServiceLayerException("使用者權限錯誤!!");
            }

	        Map<String, Map<String, Map<String, String>>> groupDeviceMap = commonService.getUserGroupAndDeviceFullInfo(prtgAccount);

            List<String> groupList = new ArrayList<>();
            for (Iterator<String> it = groupDeviceMap.keySet().iterator(); it.hasNext();) {
                groupList.add(it.next());
            }

            new PropertyDescriptor(fieldName, obj.getClass()).getWriteMethod().invoke(obj, groupList);

		} else {
			new PropertyDescriptor(fieldName, obj.getClass()).getWriteMethod().invoke(obj, queryGroup);
		}
	}

	protected void setQueryDeviceList(HttpServletRequest request, Object obj, String fieldName, String queryGroup, String queryDevice) throws Exception {
		if (StringUtils.isBlank(queryDevice)) {
			//如果未選擇特定群組，則須依照使用者權限，給予可查詢的設備清單
		    String prtgAccount = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

            if (StringUtils.isBlank(prtgAccount)) {
                throw new ServiceLayerException("使用者權限錯誤!!");
            }
            
            List<String> deviceList = new ArrayList<>();
            for (Entry<String, String> deviceEntry : commonService.getUserDeviceList(prtgAccount, queryGroup).entrySet()) {
                deviceList.add(deviceEntry.getKey());
            }

            new PropertyDescriptor(fieldName, obj.getClass()).getWriteMethod().invoke(obj, deviceList);

		} else {
			new PropertyDescriptor(fieldName, obj.getClass()).getWriteMethod().invoke(obj, queryDevice);
		}
	}

	protected Map<String, String> getMenuItem(String menuCode, boolean combineOrderDotLabel) {
		Map<String, String> itemMap = null;
		try {
			itemMap = commonService.getMenuItem(menuCode, combineOrderDotLabel);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return itemMap;
	}

	protected Map<String, String> getUserGroupList(String account) {
		Map<String, String> itemMap = null;
		try {
			itemMap = commonService.findUserGroupList(account);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return itemMap;
	}
	
	protected Map<String, String> getUserRightList(String account) {
		Map<String, String> itemMap = null;
		try {
			itemMap = commonService.findUserList(account);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return itemMap;
	}
	
	protected Map<String, String> getScriptTypeList(String defaultFlag) {
		Map<String, String> typeMap = null;
		try {
			typeMap = commonService.getScriptTypeMenu(defaultFlag);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return typeMap;
	}

	protected Map<String, String> getGroupList(HttpServletRequest request) {
		Map<String, String> retMap = new LinkedHashMap<>();
		Map<String, String> groupMap = null;
		try {
		    String prtgLoginAccount = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

            if (StringUtils.isBlank(prtgLoginAccount)) {
                throw new ServiceLayerException("使用者權限錯誤!!");
            }

			groupMap = commonService.getUserGroupList(prtgLoginAccount);

			if (groupMap == null) {
				groupMap = new HashMap<>();
			}

			/*
			 * 排序設定處理
			 */
			if (Env.SORT_GROUP_MENU_BY_GROUP_NAME_INCLUDED_SEQ_NO) {
				Map<Integer, String> sortedMap = new TreeMap<>();
				Map<String, String> sortedNonNumberMap = new TreeMap<>();
				for (Map.Entry<String, String> entry : groupMap.entrySet()) {
					final String sourceMapKey = entry.getKey();
					final String sourceMapValue = entry.getValue();

					String splitSymbolWithoutRegex = Env.GROUP_NAME_SPLIT_SEQ_NO_SYMBOL.replace("\\", "");
					if (sourceMapValue.indexOf(splitSymbolWithoutRegex) != -1) {
						Integer groupSeq =
								Integer.parseInt(sourceMapValue.split(Env.GROUP_NAME_SPLIT_SEQ_NO_SYMBOL)[Env.GROUP_NAME_SPLITTED_SEQ_NO_INDEX]);
						sortedMap.put(groupSeq, sourceMapKey);

					} else {
						sortedNonNumberMap.put(sourceMapKey, sourceMapKey);
					}
				}

				for (String sourceKey : sortedMap.values()) {
					retMap.put(sourceKey, groupMap.get(sourceKey));
				}
				for (String sourceKey : sortedNonNumberMap.values()) {
					retMap.put(sourceKey, groupMap.get(sourceKey));
				}

			} else {
				for (Map.Entry<String, String> entry : groupMap.entrySet()) {
					retMap.put(entry.getKey(), entry.getValue());
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return retMap;
	}

    @RequestMapping(value = "getDeviceMenu", method = RequestMethod.POST, produces="application/json;odata=verbose")
    public @ResponseBody AppResponse getDeviceMenu(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="groupId", required=true) String groupId) {

        Map<String, String> deviceMap;
        try {
            AppResponse appResponse;
            if (StringUtils.isBlank(groupId)) {
                appResponse = new AppResponse(HttpServletResponse.SC_NO_CONTENT, "群組未選擇，設備保持為空");
                return appResponse;
            }

            String prtgLoginAccount =
                    Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

            if (StringUtils.isBlank(prtgLoginAccount)) {
                throw new ServiceLayerException("使用者權限錯誤!!");
            }

            deviceMap = commonService.getUserDeviceList(prtgLoginAccount, groupId);

            if (deviceMap != null && !deviceMap.isEmpty()) {
                appResponse = new AppResponse(HttpServletResponse.SC_OK, "取得設備清單成功");
                appResponse.putData("device",  new Gson().toJson(deviceMap));

            } else {
                appResponse = new AppResponse(HttpServletResponse.SC_NOT_FOUND, "無法取得設備清單");
            }

            return appResponse;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    protected Map<String, String> getSensorList(HttpServletRequest request, String deviceId) {
		Map<String, String> retMap = new LinkedHashMap<>();
		Map<String, String> sensorMap = null;
		try {
		    String prtgLoginAccount =
                    Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

            if (StringUtils.isBlank(prtgLoginAccount)) {
                throw new ServiceLayerException("使用者權限錯誤!!");
            }

            sensorMap = commonService.getUserSensorList(prtgLoginAccount, deviceId);

			if (sensorMap == null) {
				sensorMap = new HashMap<>();
			}

			for (Map.Entry<String, String> entry : sensorMap.entrySet()) {
				retMap.put(entry.getKey(), entry.getValue());
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return retMap;
	}
    
	public Map<String, String> getGroupDeviceMenu(HttpServletRequest request, String searchTxt, String scriptDeviceModel) throws ServiceLayerException {
		Map<String, String> menuMap = new LinkedHashMap<>();

		String prtgAccount = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

        if (StringUtils.isBlank(prtgAccount)) {
            throw new ServiceLayerException("使用者權限錯誤!!");
        }

        Map<String, Map<String, Map<String, String>>> groupDeviceMap = commonService.getUserGroupAndDeviceFullInfo(prtgAccount);

		if (groupDeviceMap != null && !groupDeviceMap.isEmpty()) {
			for (Iterator<String> it = groupDeviceMap.keySet().iterator(); it.hasNext();) {
				final String groupKey = it.next();
				Map<String, Map<String, String>> groupMap = groupDeviceMap.get(groupKey);

				String groupName;
				int idx = 0;
				for (Map<String, String> deviceMap : groupMap.values()) {
					groupName = deviceMap.get(Constants.GROUP_NAME);

					final String deviceId = deviceMap.get(Constants.DEVICE_ID);
					final String deviceName = deviceMap.get(Constants.DEVICE_ENG_NAME);
					final String deviceModel = deviceMap.get(Constants.DEVICE_MODEL);

					if (StringUtils.isBlank(searchTxt) && StringUtils.isBlank(scriptDeviceModel)) {
						if (idx == 0) {
							menuMap.put("GROUP_"+groupKey, groupName);
						}
						menuMap.put("DEVICE_"+deviceId, deviceName);
						idx++;

					} else {
						if (!StringUtils.equalsIgnoreCase(scriptDeviceModel, Constants.DATA_STAR_SYMBOL)) {
							if (!StringUtils.containsIgnoreCase(deviceModel, scriptDeviceModel)) {
								continue;
							}
						}
						if (StringUtils.isBlank(searchTxt)
							|| (StringUtils.isNotBlank(searchTxt) && StringUtils.containsIgnoreCase(groupName, searchTxt))
							|| (StringUtils.isNotBlank(searchTxt) && StringUtils.containsIgnoreCase(deviceName, searchTxt))) {
								if (idx == 0) {
									menuMap.put("GROUP_"+groupKey, groupName);
								}

								menuMap.put("DEVICE_"+deviceId, deviceName);
								idx++;
						}
					}
				}
			}
		}

		return menuMap;
	}

	@RequestMapping(value = "getGroupDeviceMenu.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse getGroupDeviceMenu(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="searchTxt", required=true) String searchTxt,
			@RequestParam(name="scriptDeviceModel", required=true) String scriptDeviceModel) {

		try {
			Map<String, String> menuMap = getGroupDeviceMenu(request, searchTxt, scriptDeviceModel);

			AppResponse appResponse = new AppResponse(HttpServletResponse.SC_OK, "取得設備清單成功");
			appResponse.putData("groupDeviceMenu",  new Gson().toJson(menuMap));

			return appResponse;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

	protected Date validateDate(String date) {
		Date d = null;
		if (null == date) {
			return d;
		}
		try {
			d = sdfDate.parse(date);
		} catch (ParseException e) {
			return d;
		}
		return d;
	}

	protected Date validateDateTime(String date) {
		Date d = null;
		if (null == date) {
			return d;
		}
		try {
			d = sdfDateTime.parse(date);
		} catch (ParseException e) {
			return d;
		}
		return d;
	}

	public static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	//基于Cookie的国际化处理
	protected void changeLang(HttpServletRequest request, HttpServletResponse response, Model model, String langType) {
		if (!model.containsAttribute("contentModel")) {
			if (langType.contains("zh")) {
				Locale locale = new Locale("zh", "CN");
				(new CookieLocaleResolver()).setLocale(request, response, locale);
			} else if (langType.equals("en_US")) {
				Locale locale = new Locale("en", "US");
				(new CookieLocaleResolver()).setLocale(request, response, locale);
			} else {
				(new CookieLocaleResolver()).setLocale(request, response, LocaleContextHolder.getLocale());
			}
		}
	}

	protected void convertJson2POJO(Object pojo, final JsonNode jsonData) {

		Iterator<String> it = jsonData.fieldNames();

		while (it.hasNext()) {
			try {
				final String fieldName = it.next();
				Class<?> fieldNameType = pojo.getClass().getDeclaredField(fieldName).getType();

				final JsonNode fieldNode = jsonData.findValue(fieldName);

				if (fieldNameType.isAssignableFrom(String.class)) {
					PropertyUtils.setProperty(pojo, fieldName, fieldNode.asText());

				} else if (fieldNameType.isAssignableFrom(Integer.class)) {
					PropertyUtils.setProperty(pojo, fieldName, Integer.parseInt(fieldNode.asText()));

				} else if (fieldNameType.isAssignableFrom(List.class)) {
					String[] nodeValues = null;

					if (fieldName.equals("inputSysCheckSql")) {
						if (fieldNode.asText().indexOf("\r\n") != -1) {
							String[] tmp = fieldNode.asText().split("\r\n");

							List<String> sqlList = new ArrayList<>();
							StringBuffer sb = new StringBuffer();
							for (String str : tmp) {
								sb.append(str).append("\r\n");

								if (str.equals(";") || (!str.equals(";") && str.contains(";"))) {
									sqlList.add(sb.toString());
									sb.setLength(0);
								}
							}

							nodeValues = new String[sqlList.size()];
							for (int i=0; i<sqlList.size(); i++) {
								nodeValues[i] = sqlList.get(i);
							}

						} else {
							nodeValues = new String[] {fieldNode.asText()};
						}

					} else {
						if (fieldNode.asText().indexOf("\r\n") != -1) {
							nodeValues = fieldNode.asText().split("\r\n");
						} else {
							nodeValues = new String[] {fieldNode.asText()};
						}
					}

					List<String> list = new ArrayList<>();

					if (nodeValues != null) {
						for (String value : nodeValues) {
							list.add(value);
						}
					}

					PropertyUtils.setProperty(pojo, fieldName, list);
				}

			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}

	protected String getIp(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				int index = ip.indexOf(",");
				if (index != -1) {
					return ip.substring(0, index);
				} else {
					return ip;
				}
			}
		}

		ip = request.getHeader("X-Real-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}

		ip = request.getHeader("Proxy-Client-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}

		ip = request.getHeader("WL-Proxy-Client-IP");
		if (ip != null) {
			if (!ip.isEmpty() && !"unKnown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}

		ip = request.getRemoteAddr();
		return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
	}

	protected String getMac(String ipAddr) {
		String mac = "";
        try {
        	Process p = Runtime.getRuntime().exec("arp -n");
            InputStreamReader ir = new InputStreamReader(p.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
			p.waitFor();

            boolean flag = true;
			String ipStr = "(" + ipAddr + ")";
            while(flag) {
                String str = input.readLine();
                if (str != null) {
					if (str.indexOf(ipStr) > 1) {
                        int temp = str.indexOf("at");
                        mac = str.substring(
                        temp + 3, temp + 20);
                        break;
                    }
                } else {
					flag = false;
				}
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(System.out);
        }
        return mac;
    }

	protected String getIpFromInfo(String ipAddress) {
	    String resultStr = null;

	    try {
	        CloseableHttpClient httpclient = CloseableHttpClientUtils.prepare();

	        HttpGet httpGet = new HttpGet(Env.GET_IP_FROM_INFO_API_URL + ipAddress);

	        RequestConfig requestConfig = RequestConfig.custom()
	                .setConnectTimeout(Env.HTTP_CONNECTION_TIME_OUT)            //設置連接逾時時間，單位毫秒。
	                .setConnectionRequestTimeout(Env.HTTP_CONNECTION_TIME_OUT)  //設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共用連接池的。
	                .setSocketTimeout(Env.HTTP_SOCKET_TIME_OUT)                 //請求獲取資料的超時時間，單位毫秒。 如果訪問一個介面，多少時間內無法返回資料，就直接放棄此次調用。
	                .build();
	        httpGet.setConfig(requestConfig);

	        log.info("Executing request " + httpGet.getRequestLine());

	        // Create a custom response handler
	        ResponseHandler<String> responseHandler = response -> {
	            int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode >= 200 && statusCode < 300) {
	                HttpEntity entity = response.getEntity();
	                return entity != null ? EntityUtils.toString(entity) : null;
	            } else {
	                throw new ClientProtocolException("Unexpected response status: " + statusCode);
	            }
	        };
	        resultStr = httpclient.execute(httpGet, responseHandler);

	    } catch (Exception e) {
            log.error(e.toString(), e);
        }
	    return resultStr;
	}

	protected Integer getDataExportRecordCount(String exportRecordCount) {
	    if (!StringUtils.equals(exportRecordCount, Constants.DATA_STAR_SYMBOL)) {
	        return Integer.valueOf(exportRecordCount);

	    } else {
	        return null;
	    }
	}

	protected String getFileName(String oriName, String... var) {
	    String retVal = oriName;
	    try {
	        SimpleDateFormat FORMAT_YYYYMMDD_HH24MISS = new SimpleDateFormat("yyyyMMddHHmmss");

	        if (StringUtils.indexOf(retVal, "[CurrentTime]") != -1) {
	            retVal = retVal.replace("[CurrentTime]", FORMAT_YYYYMMDD_HH24MISS.format(new Date()));
	        }
	        if (StringUtils.indexOf(retVal, "[Var1]") != -1) {
	            retVal = retVal.replace("[Var1]", var[0]);
	        }

	    } catch (Exception e) {
	        log.error(e.toString(), e);
	    }
	    return retVal;
	}
	
	protected void behaviorLog(HttpServletRequest request) {
		UserBehaviorLog entity = new UserBehaviorLog();
		entity.setLogId(UUID.randomUUID().toString());
		SecurityUser sUser = SecurityUtil.getSecurityUser();
		try {
			if(sUser != null) {
				entity.setUserAccount(SecurityUtil.getSecurityUser().getUser().getUserName());
				entity.setUserName(SecurityUtil.getSecurityUser().getUsername());
			}else {
				entity.setUserAccount(getIp(request));
				entity.setUserName("");
			}
			
		} catch (Exception e) {
			log.error(e.getMessage());
			entity.setUserAccount("");
			entity.setUserName("");
		}
		
		entity.setTargetPath(request.getRequestURI());
		entity.setDescription(request.getQueryString());
		entity.setBehaviorTime(new Timestamp((new Date()).getTime()));
		entity.setBehavior(BehaviorType.CLICK_ACTION.toString());
		userService.saveOrUpdateEntity(entity);
	}
}