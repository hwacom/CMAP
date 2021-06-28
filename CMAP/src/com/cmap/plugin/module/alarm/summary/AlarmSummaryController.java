package com.cmap.plugin.module.alarm.summary;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

@Controller
@RequestMapping("/plugin/module/alarmSummary")
public class AlarmSummaryController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private AlarmSummaryService alarmSummaryService;
		
	@Autowired
	private DatabaseMessageSourceBase messageSource;

	/**
	 * 初始化選單
	 * 
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("userAccount", SecurityUtil.getSecurityUser().getUser().getUserName());
			model.addAttribute("userGroup", SecurityUtil.getSecurityUser().getUser().getPrtgLoginAccount());
			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			
			model.addAttribute("inputQueryStatus", getMenuItem("ALARM_STATUS", false));
			model.addAttribute("inputQuerySensorType", getMenuItem("ALARM_SENSOR_TYPE", false));
			model.addAttribute("inputQueryGroup", getUserGroupList((boolean)request.getSession().getAttribute(Constants.ISADMIN)?null:SecurityUtil.getSecurityUser().getUser().getUserName()));
			model.addAttribute("inputQueryOwner", getUserRightList(null));
			model.addAttribute("ticketFlag", Env.SHOW_MENU_ITEM_PLUGIN_TICKETS);
		}
		behaviorLog(request);
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_alarm_summary";
	}


	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "queryDateBegin", required = true, defaultValue = "") String queryDateBegin,
			@RequestParam(name = "queryDateEnd", required = false, defaultValue = "") String queryDateEnd,
			@RequestParam(name = "queryTimeBegin", required = true, defaultValue = "") String queryTimeBegin,
			@RequestParam(name = "queryTimeEnd", required = false, defaultValue = "") String queryTimeEnd,
			@RequestParam(name = "querySensorType", required = false, defaultValue = "") String querySensorType,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus,
			@RequestParam(name = "queryDataStatus", required = false, defaultValue = "") String queryDataStatus,
			@RequestParam(name = "queryMessage", required = false, defaultValue = "") String queryMessage){

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    
        try {
        	if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("date", Locale.TAIWAN, null);
				AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
	            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, msg);
	            return app;
			}
			if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("time", Locale.TAIWAN, null);
				AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
	            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, msg);
	            return app;
			}

			AlarmSummaryVO asVO = new AlarmSummaryVO();
			asVO.setQueryDateBegin(queryDateBegin);
			asVO.setQueryDateEnd(queryDateEnd);
			asVO.setQueryTimeBegin(queryTimeBegin);
			asVO.setQueryTimeEnd(queryTimeEnd);
			asVO.setQuerySensorType(querySensorType);
			asVO.setQueryStatus(queryStatus);
			if(StringUtils.isNotBlank(queryDataStatus)) {
				asVO.setQueryDataStatus(StringUtils.equalsAnyIgnoreCase(queryDataStatus, "active")
						? Arrays.asList(Constants.ALARM_SUMMARY_DATA_STATUS_ACTIVE, Constants.ALARM_SUMMARY_DATA_STATUS_DOING)
						: Arrays.asList(Constants.ALARM_SUMMARY_DATA_STATUS_FINISH));
			}
			asVO.setQueryMessage(queryMessage);
			filteredTotal = alarmSummaryService.countModuleAlarmSummary(asVO);
			
            retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

        } catch (ServiceLayerException sle) {
        	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        } finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "getAlarmData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getAlarmData(Model model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(name = "queryDateBegin", required = true, defaultValue = "") String queryDateBegin,
			@RequestParam(name = "queryDateEnd", required = false, defaultValue = "") String queryDateEnd,
			@RequestParam(name = "queryTimeBegin", required = true, defaultValue = "") String queryTimeBegin,
			@RequestParam(name = "queryTimeEnd", required = false, defaultValue = "") String queryTimeEnd,
			@RequestParam(name = "querySensorType", required = false, defaultValue = "") String querySensorType,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus,
			@RequestParam(name = "queryDataStatus", required = false, defaultValue = "") String queryDataStatus,
			@RequestParam(name = "queryMessage", required = false, defaultValue = "") String queryMessage,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer startNum,
			@RequestParam(name = "length", required = false, defaultValue = "100") Integer pageLength,
			@RequestParam(name = "order[0][column]", required = false, defaultValue = "0") Integer orderColIdx,
			@RequestParam(name = "order[0][dir]", required = false, defaultValue = "desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<AlarmSummaryVO> dataList = new ArrayList<>();
		try {
			if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("date", Locale.TAIWAN, null);
				return new DatatableResponse(new Long(0), new ArrayList<AlarmSummaryVO>(), new Long(0), msg);
			}
			if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("time", Locale.TAIWAN, null);
				return new DatatableResponse(new Long(0), new ArrayList<AlarmSummaryVO>(), new Long(0), msg);
			}

			dataList = doDataQuery(queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, querySensorType,
					queryStatus, queryDataStatus, queryMessage, startNum, pageLength, orderColIdx, orderDirection);
			filteredTotal = dataList.size();
			total = dataList.size();

//			File dir = new File("");
//			Process process = Runtime.getRuntime().exec("", null, dir);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, "");
	}

	private List<AlarmSummaryVO> doDataQuery(String queryDateBegin, String queryDateEnd, String queryTimeBegin,
			String queryTimeEnd, String querySensorType, String queryStatus, String queryDataStatus, String queryMessage, Integer startNum,
			Integer pageLength, Integer orderColIdx, String orderDirection) throws ServiceLayerException {

		String[] orderCol = new String[]{"SENSOR_NAME", "SENSOR_TYPE", "GROUP_NAME", "DEVICE_NAME", "ALARM_STATUS", "ALARM_TIME", "CLOSE_TIME" , "LAST_VALUE", "MESSAGE", "PRIORITY", "REMARK", "ALARM_ID", "GROUP_ID", "DEVICE_ID", "SENSOR_ID", "ALARM_DATA_STATUS", "update_time", "update_by"};
		//ALARM_ID '11', GROUP_ID '12', DEVICE_ID '13', SENSOR_ID '14', "ALARM_DATA_STATUS", update_time '15', update_by '16'
		AlarmSummaryVO asVO = new AlarmSummaryVO();
		asVO.setQueryDateBegin(queryDateBegin);
		asVO.setQueryDateEnd(queryDateEnd);
		asVO.setQueryTimeBegin(queryTimeBegin);
		asVO.setQueryTimeEnd(queryTimeEnd);
		asVO.setQuerySensorType(querySensorType);
		asVO.setQueryStatus(queryStatus);
		if(StringUtils.isNotBlank(queryDataStatus)) {
			asVO.setQueryDataStatus(StringUtils.equalsAnyIgnoreCase(queryDataStatus, "active")
					? Arrays.asList(Constants.ALARM_SUMMARY_DATA_STATUS_ACTIVE, Constants.ALARM_SUMMARY_DATA_STATUS_DOING)
					: Arrays.asList(Constants.ALARM_SUMMARY_DATA_STATUS_FINISH));
		}
		asVO.setQueryMessage(queryMessage);
		asVO.setStartNum(startNum);
		asVO.setPageLength(pageLength);
		asVO.setOrderColumn(orderCol[orderColIdx]);
		asVO.setOrderDirection(orderDirection);

		return alarmSummaryService.findModuleAlarmSummary(asVO, startNum, pageLength);

	}
	
	@RequestMapping(value = "updateStatus", method = RequestMethod.POST)
	public @ResponseBody AppResponse saveAlarm(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			List<AlarmSummaryVO> resultList = setValue(jsonData);
			alarmSummaryService.saveOrUpdateAlarmSummary(resultList, jsonData);
			return new AppResponse(HttpServletResponse.SC_OK, "SUCCESS!!");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}

	@RequestMapping(value = "addNewTicket", method = RequestMethod.POST)
	public @ResponseBody AppResponse addNewTicket(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			alarmSummaryService.createTicket(jsonData);
			
			return new AppResponse(HttpServletResponse.SC_OK, "SUCCESS!!");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}
	
	private List<AlarmSummaryVO> setValue(JsonNode jsonData) {
		Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();
		List<Long> ids = new ArrayList<>();
		while (idIt.hasNext()) {
			ids.add(idIt.next().asLong());
		}
		
		String updateStatus = jsonData.has("updateStatus") ? jsonData.findValue("updateStatus").asText() : null;

		List<AlarmSummaryVO> resultList = new ArrayList<>();
		AlarmSummaryVO vo = null;
		for(Long inputId : ids) {
			vo = new AlarmSummaryVO();
			vo.setQueryAlarmId(inputId);
			vo.setAlarmDataStatus(updateStatus);
			
			resultList.add(vo);
		}

		return resultList;
	}
	
	@RequestMapping(value = "getAlarmLog.json", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody AppResponse getAlarmLog(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {
		String retVal = "N/A";
		
		try {
			Long queryAlarmId = jsonData.has("alarmId") ? jsonData.findValue("alarmId").asLong() : null;
//			ModuleAlarmSummary list = alarmSummaryService.findAlarmSummary(queryAlarmId);
			List<ModuleAlarmSummaryLog> logList = alarmSummaryService.findModuleAlarmSummaryLog(queryAlarmId);
			
			if(logList.isEmpty()) return new AppResponse(HttpServletResponse.SC_FOUND, "no log!");
			
			StringBuffer historyContent = new StringBuffer();
			for(ModuleAlarmSummaryLog detail : logList) {
				historyContent.append("<div class=\"entry text-with-hr\"><div class=\"modified\"><b><span>");
				historyContent.append(messageSource.getMessage("update.by", Locale.TAIWAN, null)).append("：").append(detail.getCreateBy()).append("&nbsp;&nbsp;&nbsp;");
				historyContent.append(messageSource.getMessage("create.time", Locale.TAIWAN, null)).append("：").append(Constants.FORMAT_YYYYMMDD_HH24MISS.format(detail.getCreateTime())).append("&nbsp;&nbsp;&nbsp;");
				historyContent.append("</span> </b></div><div class=\"content\">");
				historyContent.append(detail.getContent());
				historyContent.append("</div></div><br/>");
			}
			
			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, historyContent.toString());
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

//        } catch (ServiceLayerException sle) {
//        	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        } finally {
			initMenu(model, request);
		}
	}
	

	/**
	 * 資料匯出
	 * @param model
	 * @param request
	 * @param response
	 * @param queryDateBegin
	 * @param queryDateEnd
	 * @param queryTimeBegin
	 * @param queryTimeEnd
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @param exportRecordCount
	 * @return
	 */
//	@RequestMapping(value = "dataExport.json", method = RequestMethod.POST)
//    public @ResponseBody AppResponse dataExport(
//            Model model, HttpServletRequest request, HttpServletResponse response,
//            @RequestParam(name = "queryDateBegin", required = true, defaultValue = "") String queryDateBegin,
//			@RequestParam(name = "queryDateEnd", required = false, defaultValue = "") String queryDateEnd,
//			@RequestParam(name = "queryTimeBegin", required = true, defaultValue = "") String queryTimeBegin,
//			@RequestParam(name = "queryTimeEnd", required = false, defaultValue = "") String queryTimeEnd,
//			@RequestParam(name = "queryMessage", required = false, defaultValue = "") String queryMessage,
//			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus,
//            @RequestParam(name = "order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
//            @RequestParam(name = "order[0][dir]", required=false, defaultValue="desc") String orderDirection,
//            @RequestParam(name = "exportRecordCount", required=true, defaultValue="") String exportRecordCount) {
//
//	    try {
//            if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
//				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
//						+ messageSource.getMessage("date", Locale.TAIWAN, null);
//				return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, msg);
//			}
//			if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
//				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
//						+ messageSource.getMessage("time", Locale.TAIWAN, null);
//				return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, msg);
//			}
//
//			Integer queryStartNum = 0;
//            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);
//
//			List<AlarmSummaryVO> dataList = doDataQuery(queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryStatus, queryMessage,
//					queryStartNum, queryPageLength, orderColIdx, orderDirection);
//
//	        if (dataList != null && !dataList.isEmpty()) {
//				String fileName = getFileName("alarmSummary_[CurrentTime]");
//				String[] fieldNames = new String[] { "alarmId", "sensorName", "groupName", "deviceName",
//						"alarmDataStatus", "alarmStatus", "alarmTime", "closeTime", "lastValue", "message", "priority",
//						"remark", "updateTime", "updateBy" };
//				String[] columnsTitles = new String[] { "警報編號", "感測器名稱", "群組名稱", "設備名稱", "狀態", "警報狀態", "警報時間", "結案時間",
//						"最後監控數值", "訊息", "主旨", "備註", "最後更新時間", "最後更新人員" };
//
//	            DataExportUtils export = new CsvExportUtils();
//	            String fileId = export.output2Web(response, fileName, true, dataList, fieldNames, columnsTitles);
//
//	            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
//	            app.putData("fileId", fileId);
//                return app;
//
//	        } else {
//	            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "No matched data.");
//	            return app;
//	        }
//
//	    } catch (Exception e) {
//	        log.error(e.toString(), e);
//	        AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
//            return app;
//	    } finally {
//			initMenu(model, request);
//		}
//	}
}
