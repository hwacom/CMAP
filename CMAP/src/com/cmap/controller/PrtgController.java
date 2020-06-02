package com.cmap.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.security.SecurityUtil;
import com.cmap.service.CommonService;
import com.cmap.service.PrtgService;
import com.cmap.service.UserService;
import com.cmap.service.vo.PrtgServiceVO;
import com.cmap.utils.ApiUtils;
import com.cmap.utils.impl.CloseableHttpClientUtils;
import com.cmap.utils.impl.PrtgApiUtils;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/prtg")
public class PrtgController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private CommonService commonService;

	private void init(Model model) {
		model.addAttribute("PRTG_IP_ADDR", Env.PRTG_SERVER_IP);
		model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
	}

//	private String sendLogin(HttpServletRequest request, HttpServletResponse response) {
//		String retVal = "";
//		HttpSession session = request.getSession();
//
//		try {
//			final String sourceId = Objects.toString(session.getAttribute(Constants.OIDC_SCHOOL_ID), null);
//			PrtgServiceVO psVO = commonService.findPrtgLoginInfo(sourceId);
//
//			if (psVO != null) {
//				final String account = psVO.getAccount();
//				final String password = psVO.getPassword();
//
//				CloseableHttpClient httpclient = CloseableHttpClientUtils.prepare();
//
//				/*
//				 * Step 1. Login
//				 */
//				HttpPost httpPost = new HttpPost(Env.PRTG_LOGIN_URI);
//
//				RequestConfig requestConfig = RequestConfig.custom()
//						.setConnectTimeout(Env.HTTP_CONNECTION_TIME_OUT)				//設置連接逾時時間，單位毫秒。
//						.setConnectionRequestTimeout(Env.HTTP_CONNECTION_TIME_OUT)	//設置從connect Manager獲取Connection 超時時間，單位毫秒。這個屬性是新加的屬性，因為目前版本是可以共用連接池的。
//						.setSocketTimeout(Env.HTTP_SOCKET_TIME_OUT)					//請求獲取資料的超時時間，單位毫秒。 如果訪問一個介面，多少時間內無法返回資料，就直接放棄此次調用。
//						.build();
//				httpPost.setConfig(requestConfig);
//
//				List<NameValuePair> params = new ArrayList<>();
//				params.add(new BasicNameValuePair("loginurl", "/welcome.htm"));
//	            params.add(new BasicNameValuePair("username", account));
//	            params.add(new BasicNameValuePair("password", password));
//
//	            HttpEntity httpEntity = new UrlEncodedFormEntity(params, "UTF-8");
//	            httpPost.setEntity(httpEntity);
//
//				log.info("Executing request " + httpPost.getRequestLine());
//
//				HttpClientContext context = HttpClientContext.create();
//				CloseableHttpResponse closeableResponse = httpclient.execute(httpPost, context);
//
//				CookieStore cookieStore = context.getCookieStore();
//				try {
//					int statusCode = closeableResponse.getStatusLine().getStatusCode();
//					log.info(">>>>>>>>>>>>>>>>>> statusCode: " + statusCode);
//
//					javax.servlet.http.Cookie httpCookie;
//				    List<Cookie> cookies = cookieStore.getCookies();
//				    for (Cookie c : cookies) {
//				    	log.info(c.toString());
//
//				    	System.out.println("Name: " + c.getName() + ", Value: " + c.getValue());
//				    	httpCookie = new javax.servlet.http.Cookie(c.getName(), c.getValue());
//				    	response.addCookie(httpCookie);
//				    }
//
//				} finally {
//					closeableResponse.close();
//				}
//			}
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//
//		return retVal;
//	}
//
//	@RequestMapping(value = "/welcomePage", method = RequestMethod.GET)
//	public String welcomePage(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
//		try {
//			String html = sendLogin(request, response);
//			model.addAttribute("IFRAME_HTML", html);
//
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//		return "prtg/welcome";
//	}

	@RequestMapping(value = "getLoginUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getLoginUri(
			Model model, HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		try {
			final String BASE_URI = Env.PRTG_LOGIN_URI;
			final String PRTG_ACCOUNT = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");
			final String PRTG_PASSWORD = Objects.toString(session.getAttribute(Constants.PRTG_LOGIN_PASSWORD), "");
			final String prtgIndexUri = BASE_URI + "?a=" + PRTG_ACCOUNT + "&p=" + PRTG_PASSWORD;

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", prtgIndexUri);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	@RequestMapping(value = "getPrtgIndexUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getPrtgIndexUri(Model model, HttpServletRequest request, HttpServletResponse response) {

		try {
			String indexUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_INDEX_URI);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", indexUrl);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	/**
	 * 組合最終 API URL for 設定的URL內是否有加上參數
	 * @param request
	 * @param oriUrl
	 * @return
	 */
	private String composePrtgUrl(HttpServletRequest request, String oriUrl) {
	    String username = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), null);
	    String password = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_PASSWORD), null);
	    String passhash = Objects.toString(request.getSession().getAttribute(Constants.PASSHASH), null);
	    oriUrl = StringUtils.replace(oriUrl, "{username}", username);
	    oriUrl = StringUtils.replace(oriUrl, "{password}", password);
	    oriUrl = StringUtils.replace(oriUrl, "{passhash}", passhash);
	    return oriUrl;
	}

	@RequestMapping(value = "getPrtgDashboardUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getPrtgDashboardUri(Model model, HttpServletRequest request, HttpServletResponse response) {

		try {
			String dashboardMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_DASHBOARD_URI);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", dashboardMapUrl);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	@RequestMapping(value = "getPrtgTopographyUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgTopographyUri(Model model, HttpServletRequest request, HttpServletResponse response) {
		
        try {
            String topographyMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_TOPOGRAPHY_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", topographyMapUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }

	@RequestMapping(value = "getPrtgAlarmSummaryUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgAlarmSummaryUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            String alarmSummaryMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_ALARM_SUMMARY_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", alarmSummaryMapUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }

	@RequestMapping(value = "getPrtgNetFlowSummaryUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getPrtgNetFlowSummaryUri(Model model, HttpServletRequest request, HttpServletResponse response) {

		try {
			String netFlowSummaryMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_NET_FLOW_SUMMARY_URI);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", netFlowSummaryMapUrl);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	@RequestMapping(value = "getPrtgNetFlowOutputUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgNetFlowOutputUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
        	String netFlowOutputMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_NET_FLOW_OUTPUT_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", netFlowOutputMapUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }

	@RequestMapping(value = "getPrtgNetFlowOutputCoreUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgNetFlowOutputCoreUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            String netFlowOutputMapCoreUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_NET_FLOW_OUTPUT_CORE_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", netFlowOutputMapCoreUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }

	@RequestMapping(value = "getPrtgFirewallOutputUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgFirewallOutputUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            String firewallOutputUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_FIREWALL_OUTPUT_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", firewallOutputUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }
	
	@RequestMapping(value = "getPrtgLoopSearchUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getPrtgLoopSearchUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            String loopSearchUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_LOOP_SEARCH_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", loopSearchUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }
	
	@RequestMapping(value = "getPrtgDeviceFailureUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getPrtgDeviceFailureUri(Model model, HttpServletRequest request, HttpServletResponse response) {

		try {
			String deviceFailureMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_DEVICE_FAILURE_URI);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", deviceFailureMapUrl);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	@RequestMapping(value = "getPrtgAbnormalTrafficUri", method = RequestMethod.POST)
	public @ResponseBody AppResponse getPrtgAbnormalTrafficUri(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		try {
			String abnormalTrafficMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_ABNORMAL_TRAFFIC_URI);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
			app.putData("uri", abnormalTrafficMapUrl);
			return app;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
		}
	}

	@RequestMapping(value = "getEmailUpdateUri", method = RequestMethod.POST)
    public @ResponseBody AppResponse getEmailUpdateUri(Model model, HttpServletRequest request, HttpServletResponse response) {

        try {
            String emailUpdateMapUrl = composePrtgUrl(request, Env.PRTG_DEFAULT_EMAIL_UPDATE_URI);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "success");
            app.putData("uri", emailUpdateMapUrl);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());

        } finally {
        }
    }

	@RequestMapping(value = "/index/login", method = RequestMethod.GET)
	public String prtgIndexAndLogin(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);
			model.addAttribute("DO_LOGIN", "Y");

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String prtgIndex(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/index";
	}

	@RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	public String prtgDashboard(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/dashboard";
	}

	@RequestMapping(value = "/topography", method = RequestMethod.GET)
    public String prtgTopography(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/topography";
    }

	@RequestMapping(value = "/alarmSummary", method = RequestMethod.GET)
    public String prtgAlarmSummary(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/alarm_summary";
    }

	@RequestMapping(value = "/netFlowSummary", method = RequestMethod.GET)
	public String prtgNetFlowSummary(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/net_flow_summary";
	}

	@RequestMapping(value = "/netFlowOutput", method = RequestMethod.GET)
    public String prtgNetFlowOutput(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/net_flow_output";
    }

	@RequestMapping(value = "/loopSearch", method = RequestMethod.GET)
    public String prtgLoopSearch(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/loop_search";
    }
	
	@RequestMapping(value = "/netFlowOutput/core", method = RequestMethod.GET)
    public String prtgNetFlowOutputCore(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/net_flow_output_core";
    }

	@RequestMapping(value = "/deviceFailure", method = RequestMethod.GET)
	public String prtgDeviceFailure(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/device_failure";
	}

	@RequestMapping(value = "/abnormalTraffic", method = RequestMethod.GET)
	public String prtgAbnormalTraffic(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			init(model);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "prtg/abnormal_traffic";
	}

	@RequestMapping(value = "/firewallOutput", method = RequestMethod.GET)
    public String prtgFirewallOutput(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/firewall_output";
    }

	@RequestMapping(value = "/email/update", method = RequestMethod.GET)
    public String prtgEmailUpdate(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {
            init(model);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return "prtg/email_update";
    }
}
