package com.cmap.plugin.module.mobilepoller;

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
import com.cmap.security.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/plugin/module/mobilePoller")
public class MobilePollerController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private DatabaseMessageSourceBase messageSource;
	
	@Autowired
	private MobilePollerService mobilePollerService;
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
		//Map<String, String> deviceListMap = null;
		try {
			// 2020-07-01 modified by Alvin 修正為一律載入GroupList不受SensorMode影響
			/*
			if(isSensorSearchMode) {
				if(StringUtils.isBlank(Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW)) {
					sensorListMap = getSensorList(request, null);
				}else {
					sensorListMap = getSensorList(request, Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW);
				}
			}else {
				groupListMap = getGroupList(request);
			}
			*/
			groupListMap = getGroupList(request);
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("queryGroup", "");
			// 2020-07-01 modified by Alvin 修正為一律載入GroupList不受SensorMode影響
			/*
			if(isSensorSearchMode) {
				model.addAttribute("groupList", sensorListMap);
			}else {
				model.addAttribute("groupList", groupListMap);
			}	
			*/
			model.addAttribute("groupList", groupListMap);
			
			model.addAttribute("isSensorSearchMode", isSensorSearchMode);

			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
		}
	}


	@RequestMapping(value = "", method = RequestMethod.GET)
	public String mobilePoller(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_mobile_poller";
	}

	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientSUPI", required=false, defaultValue="") String queryClientSUPI,
			@RequestParam(name="queryClientNumber", required=false, defaultValue="") String queryClientNumber,
			@RequestParam(name="queryCellName", required=false, defaultValue="") String queryCellName,
			@RequestParam(name="querySsid", required=false, defaultValue="") String querySsid) {

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    MobilePollerVO searchVO;
        try {
        	searchVO = new MobilePollerVO();
        	searchVO.setQueryGroupId(queryGroupId);
        	searchVO.setQueryDateBegin(queryDateBegin);
        	searchVO.setQueryDateEnd(queryDateEnd);
        	searchVO.setQueryTimeBegin(queryTimeBegin);
        	searchVO.setQueryTimeEnd(queryTimeEnd);
        	searchVO.setQueryClientSUPI(queryClientSUPI);
        	searchVO.setQueryClientNumber(queryClientNumber);
        	searchVO.setQueryCellName(queryCellName);
        	searchVO.setQuerySsid(querySsid);
        	
	        filteredTotal = this.mobilePollerService.countMobileMstRecordFromDB(searchVO);
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
	
	@RequestMapping(value = "getMobileMstData.json", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody DatatableResponse getMobileMstData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientSUPI", required=false, defaultValue="") String queryClientSUPI,
			@RequestParam(name="queryClientNumber", required=false, defaultValue="") String queryClientNumber,
			@RequestParam(name="queryCellName", required=false, defaultValue="") String queryCellName,
			@RequestParam(name="querySsid", required=false, defaultValue="") String querySsid,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		String totalFlow = "";
		List<MobilePollerVO> dataList = new ArrayList<>();
		try {
		    /*
			if (StringUtils.isBlank(queryGroup)) {
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
			
	        MobilePollerVO resultVO = doDataQuery(queryGroupId, queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryClientSUPI,
	        		queryClientNumber, queryCellName, querySsid, startNum, pageLength,  orderColIdx, orderDirection );
	        
	        filteredTotal = resultVO.getMatchedList().size();
            dataList = resultVO.getMatchedList();
            total = resultVO.getTotalCount();

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, totalFlow);
	}
	
	
	private MobilePollerVO doDataQuery(String queryGroupId, String queryDateBegin, String queryDateEnd, String queryTimeBegin, String queryTimeEnd, String queryClientSUPI,
			String queryClientNumber, String queryCellName, String querySsid, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws ServiceLayerException {

			MobilePollerVO retVO = new MobilePollerVO();
		    try {
		        	MobilePollerVO searchVO = new MobilePollerVO();
		        	searchVO.setQueryGroupId(queryGroupId);
		        	searchVO.setQueryDateBegin(queryDateBegin);
		        	searchVO.setQueryDateEnd(queryDateEnd);
		        	searchVO.setQueryTimeBegin(queryTimeBegin);
		        	searchVO.setQueryTimeEnd(queryTimeEnd);
		        	searchVO.setQueryClientSUPI(queryClientSUPI);
		        	searchVO.setQueryClientNumber(queryClientNumber);
		        	searchVO.setQueryCellName(queryCellName);
		        	searchVO.setQuerySsid(querySsid);
		        	searchVO.setStartNum(startNum);
		        	searchVO.setPageLength(pageLength);
		        	searchVO.setOrderColumn(orderColIdx.toString()); // DAO SQL用別名同orderColIdx即可
		        	searchVO.setOrderDirection(orderDirection);
			        
		            List<MobilePollerVO> dataList = new ArrayList<>();
		            dataList = this.mobilePollerService.findModuleMobileTraceMst(searchVO, startNum, pageLength);
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
	@RequestMapping(value = "getMobileDetailData.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getMobileDetailData(
    		Model model, HttpServletRequest request, HttpServletResponse response,
    		@RequestBody JsonNode jsonData) {
    		//查詢條件
    		String queryClientSUPI = jsonData.findValues("clientSUPI").get(0).asText();
    		String queryStartTime = jsonData.findValues("startTime").get(0).asText();
    		String queryEndTime = jsonData.findValues("endTime").get(0).asText();
    		//顯示資訊
		 	String clientNumber = jsonData.findValues("clientNumber").get(0).asText();
    		String groupName = jsonData.findValues("groupName").get(0).asText();

    		Map<String, List<Map<String, String>>> detailDataMap = new HashMap<>();
    		try {
    			MobilePollerVO searchVO = new MobilePollerVO();
    			
    			searchVO.setQueryClientSUPI(queryClientSUPI);
    			searchVO.setQueryStartTime(queryStartTime);
    			searchVO.setQueryEndTime(queryEndTime);

    			detailDataMap = this.mobilePollerService.findModuleMobileTraceDetail(searchVO);

    			Map<String, Object> retMap = new HashMap<>();
    			retMap.put("groupName", groupName);
    			retMap.put("clientNumber", clientNumber);
    			retMap.put("clientSUPI", queryClientSUPI);
    			retMap.put("startTime", queryStartTime);
    			if (StringUtils.isNotBlank(queryEndTime) ) {
    				 retMap.put("endTime", queryEndTime);
    			 }else {
    				 retMap.put("endTime", "now");
    			 }
    			// 有查回detailData時進行JSON格式轉換
    			if(detailDataMap != null && !detailDataMap.isEmpty()) {
    				ObjectMapper objectMapper = new ObjectMapper();
    				retMap.put("uploadTrafficDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("uploadTrafficDataList")));
    				retMap.put("downloadTrafficDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("downloadTrafficDataList")));
    				retMap.put("totalTrafficDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("totalTrafficDataList")));
    				retMap.put("rssiDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("rssiDataList")));
    				retMap.put("noiseDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("noiseDataList")));
    				retMap.put("snrDataList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDataMap.get("snrDataList")));
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
