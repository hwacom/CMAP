package com.cmap.plugin.module.iptracepoller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.plugin.module.ip.mapping.IpMappingServiceVO;
import com.cmap.security.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/plugin/module/ipTracePoller")
public class IpTracePollerController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private DatabaseMessageSourceBase messageSource;
	
	@Autowired
	private IpTracePollerService ipTracePollerService;
	//是否查詢條件為sensorId
  	private boolean isSensorSearchMode = StringUtils.equalsIgnoreCase(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR, Constants.DATA_Y);
  	
	/**
	 * 初始化選單
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
		Map<String, String> groupListMap = null;
		Map<String, String> sensorListMap = null;
		Map<String, String> deviceListMap = null;
		try {
			if(isSensorSearchMode) {
				if(StringUtils.isBlank(Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW)) {
					sensorListMap = getSensorList(request, null);
				}else {
					sensorListMap = getSensorList(request, Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW);
				}
			}else {
				groupListMap = getGroupList(request);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("queryGroup", "");
			if(isSensorSearchMode) {
				model.addAttribute("groupList", sensorListMap);
			}else {
				model.addAttribute("groupList", groupListMap);
			}	
			model.addAttribute("isSensorSearchMode", isSensorSearchMode);
			model.addAttribute("device", "");
			model.addAttribute("deviceList", deviceListMap);

			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
		}
	}


	@RequestMapping(value = "", method = RequestMethod.GET)
	public String ipTracePoller(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_ip_trace_poller";
	}

	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
    		@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientMac", required=false, defaultValue="") String queryClientMac,
			@RequestParam(name="queryClientIp", required=false, defaultValue="") String queryClientIp,
			@RequestParam(name="queryOnLineOnly", required=false, defaultValue="false") boolean queryOnLineOnly){

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    IpTracePollerVO searchVO;
        try {
        	searchVO = new IpTracePollerVO();
        	searchVO.setQueryGroupId(queryGroupId);
        	searchVO.setQueryDevice(queryDevice);
        	searchVO.setQueryDateBegin(queryDateBegin);
        	searchVO.setQueryDateEnd(queryDateEnd);
        	searchVO.setQueryTimeBegin(queryTimeBegin);
        	searchVO.setQueryTimeEnd(queryTimeEnd);
        	searchVO.setQueryClientMac(queryClientMac);
        	searchVO.setQueryClientIp(queryClientIp);
        	searchVO.setQueryOnLineOnly(queryOnLineOnly);
        	
	        filteredTotal = this.ipTracePollerService.countIpTraceDataFromDB(searchVO);
            retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        }
	}
	
	@RequestMapping(value = "getIpTraceData.json", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody DatatableResponse getIpTraceData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientMac", required=false, defaultValue="") String queryClientMac,
			@RequestParam(name="queryClientIp", required=false, defaultValue="") String queryClientIp,
			@RequestParam(name="queryOnLineOnly", required=false, defaultValue="false") boolean queryOnLineOnly,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		String totalFlow = "";
		List<IpTracePollerVO> dataList = new ArrayList<>();
		try {
		    /*
			if (StringUtils.isBlank(queryGroupId)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("group.name", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<IpTracePollerVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<IpTracePollerVO>(), new Long(0), msg);
	        }
	        */

	        IpTracePollerVO resultVO = doDataQuery( queryGroupId, queryDevice, queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryClientMac,
	        		queryClientIp, queryOnLineOnly, startNum, pageLength,  orderColIdx, orderDirection );
	        
	        filteredTotal = resultVO.getMatchedList().size();
            dataList = resultVO.getMatchedList();
            total = resultVO.getTotalCount();

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, totalFlow);
	}
	
	
	private IpTracePollerVO doDataQuery( String queryGroupId, String queryDevice, String queryDateBegin, String queryDateEnd, String queryTimeBegin, String queryTimeEnd, String queryClientMac,
			String queryClientIp, boolean queryOnLineOnly, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws ServiceLayerException {

			IpTracePollerVO retVO = new IpTracePollerVO();
		    try {
		        	IpTracePollerVO searchVO = new IpTracePollerVO();
		        	searchVO.setQueryGroupId(queryGroupId);
		        	searchVO.setQueryDevice(queryDevice);
		        	searchVO.setQueryDateBegin(queryDateBegin);
		        	searchVO.setQueryDateEnd(queryDateEnd);
		        	searchVO.setQueryTimeBegin(queryTimeBegin);
		        	searchVO.setQueryTimeEnd(queryTimeEnd);
		        	searchVO.setQueryClientMac(queryClientMac);
		        	searchVO.setQueryClientIp(queryClientIp);
		        	searchVO.setQueryOnLineOnly(queryOnLineOnly);
		        	searchVO.setStartNum(startNum);
		        	searchVO.setPageLength(pageLength);
		        	searchVO.setOrderColumn(orderColIdx.toString()); // DAO SQL用別名同orderColIdx即可
		        	searchVO.setOrderDirection(orderDirection);
			        
		            List<IpTracePollerVO> dataList = new ArrayList<>();
		            dataList = this.ipTracePollerService.findModuleIpTrace(searchVO, startNum, pageLength);
		            retVO.setMatchedList(dataList);
		            
		    } catch (ServiceLayerException sle) {
	        } catch (Exception e) {
	            log.error(e.toString(), e);
	        }
	        return retVO;
		}
	
	  /**
     * 從 NET_FLOW 查詢功能點擊 SOURCE_IP or DESTINATION_IP 連結時，查找該筆 NET_FLOW 當下 IP 對應的 PORT 資料
     * @param model
     * @param request
     * @param response
     * @param jsonData
     * @return
     */
    @RequestMapping(value = "getIpTraceDataFromNetFlow.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse getIpTraceDataFromNetFlow(
    		Model model, HttpServletRequest request, HttpServletResponse response,
    		@RequestBody JsonNode jsonData) {
    		//查詢條件
    		String queryGroupId = jsonData.findValues("groupId").get(0).asText();
    		String queryFromDateTime = jsonData.findValues("fromDateTime").get(0).asText();
    		//顯示資訊
    		String groupName = jsonData.findValues("groupName").get(0).asText();
    		String ipAddress = jsonData.findValues("ipAddress").get(0).asText();

    		IpTracePollerVO retVO = new IpTracePollerVO();
    		try {
    			IpTracePollerVO searchVO = new IpTracePollerVO();
    			searchVO.setQueryGroupId(queryGroupId);
    			searchVO.setQueryFromDateTime(queryFromDateTime);
    			searchVO.setQueryClientIp(ipAddress);

    			retVO = this.ipTracePollerService.findModuleIpTraceFromNetFlow(searchVO);

    			Map<String, Object> retMap = new HashMap<>();
    			retMap.put("groupName", groupName);
    			retMap.put("ipAddress", ipAddress);
    			retMap.put("deviceName", retVO.getDeviceName());
    			retMap.put("deviceModel", retVO.getDeviceModel());
    			retMap.put("ipDesc", retVO.getIpDesc());
    			retMap.put("portName", retVO.getPortName());
    			retMap.put("showMsg", retVO.getShowMsg());
    			//是否驗證未納管IP來源國家
			  	boolean isEnableGetIpFromInfo = StringUtils.equalsIgnoreCase(Env.ENABLE_GET_IP_FROM_INFO, Constants.DATA_Y);
			  	retMap.put("isEnableGetIpFromInfo", isEnableGetIpFromInfo);
    			if(isEnableGetIpFromInfo) {
    				String ipFromInfo = getIpFromInfo(ipAddress);
    				//有查到就將來源資料存入retMap
    				if (StringUtils.isNotBlank(ipFromInfo)) {
    					ObjectMapper mapper = new ObjectMapper();
    					JsonNode ipJsonObj = mapper.readTree(ipFromInfo);
    					if( ipJsonObj.findValue("status").asText().equals("success")) {
    						String country = ipJsonObj.findValue("country").asText();
    						String countryCode = ipJsonObj.findValue("countryCode").asText();
    						String city = ipJsonObj.findValue("city").asText();
    						String region = ipJsonObj.findValue("regionName").asText();

    						retMap.put("location", city + ", " + region + ", " + country + " (" + countryCode + ")");
    						retMap.put("countryCode", StringUtils.lowerCase(countryCode));
    					} else {
    						// 當API查詢IP status fail時，改提供備用網站連結
    						retMap.put("countryQueryURL", Env.GET_IP_FROM_INFO_WEB_SITE_URL + ipAddress);
    					}
    				} else {
    					// 當API網站發生問題時，改提供備用網站連結
    					retMap.put("countryQueryURL", Env.GET_IP_FROM_INFO_WEB_SITE_URL + ipAddress);
    				}
    			}
    			
    			return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

    		} catch (ServiceLayerException sle) {
    			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
    		} catch (Exception e) {
    			log.error(e.toString(), e);
    			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
    		}
    }
}
