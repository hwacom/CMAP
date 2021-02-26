package com.cmap.plugin.module.provision;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.model.ProvisionLogConfigBackupError;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.security.SecurityUtil;
import com.cmap.service.ProvisionApiService;
import com.cmap.service.ProvisionService;
import com.cmap.service.PrtgService;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/provision")
public class ProvisionController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private PrtgService prtgService;
	
	@Autowired
	private ProvisionApiService provisionApiService;
	
	@Autowired
	private ProvisionService provisionService;
	
	private String logKey = null;
	
	/**
	 * 
	 * @param jsonData
	 * @return
	 */
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "getScriptInfo.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String getScriptInfo(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";
		// 宣告container並填充內容，最後會以JSON格式回傳至前端
		Map<String, Object> data = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			String scriptType = jsonData.has("type")? jsonData.get("type").textValue() : null;
			String scriptCode = jsonData.has("code")? jsonData.get("code").textValue() : null;
			String username = jsonData.get("user").textValue();
			Iterator<JsonNode> idIt = jsonData.get("deviceId").iterator();

			if((StringUtils.isBlank(scriptType) && StringUtils.isBlank(scriptCode))
					|| (StringUtils.isNotBlank(scriptType) && StringUtils.isNotBlank(scriptCode))
					|| StringUtils.isBlank(username) || idIt == null || !idIt.hasNext()) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}

			data = provisionApiService.getDefaultScriptInfo(jsonData, SecurityUtil.getIpAddr(request));
            jsonString = mapper.writeValueAsString(data);

	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
            return jsonString;
	    }

	    return jsonString;
	}
	
	/**
	 * 呼叫 API 入口
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param apiVmName
	 * @return
	 */
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "doProvision.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String doProvision(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			// Step 1.
			if(jsonData == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}
			
			data = provisionApiService.doApiProvision(jsonData, SecurityUtil.getIpAddr(request));
			jsonString = mapper.writeValueAsString(data);
			
	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
            return jsonString;
	    }

	    return jsonString;
	}

	/**
	 * 
	 * @param jsonData
	 * @return
	 */
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "getScriptTypeList.json", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String getScriptTypeList(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";
		// 宣告container並填充內容，最後會以JSON格式回傳至前端
		Map<String, Object> data = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();		
		List<String> typeCode = new ArrayList<>();
		List<String> typeName = new ArrayList<>();
		
		try {
			
			// Step 1.
			if (jsonData == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
				return jsonString;
			}
			String username = jsonData.get("user").textValue();
			PrtgAccountMapping mapping = prtgService.getMappingByAccount(username);
			Map<String, String> retMap = commonService.getScriptTypeMenu(null);

			for(String key:retMap.keySet()) {
				typeCode.add(key);
				typeName.add(retMap.get(key));
			}
			
			if(!typeCode.isEmpty() && !typeName.isEmpty()) {
				data.put("typeCode", typeCode);
				data.put("typeName", typeName);
			}
			
	        jsonString = mapper.writeValueAsString(data);
	        System.out.println(new Date() + " >>> " + jsonString);
	        
	    } catch (Exception e) {
	        log.error(e.toString(), e);
	        data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
	        try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
	        return jsonString;
	    }

		return jsonString;
	}
	
	/**
	 * 呼叫 API 入口
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param apiVmName
	 * @return
	 */
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "doConfigBackup", method = RequestMethod.POST, produces="application/json;charset=UTF-8")
	public @ResponseBody String doConfigBackup(HttpServletRequest request, @RequestBody JsonNode jsonData) {

		String jsonString = "";		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			// Step 1.
			if(jsonData == null) {
				data.put("infoMsg", "error");
				data.put("errMsg", "參數不正確!!");
				jsonString = mapper.writeValueAsString(data);
	            return jsonString;
			}
			
			data = provisionApiService.doApiConfigBackup(jsonData, SecurityUtil.getIpAddr(request));
			jsonString = mapper.writeValueAsString(data);
			
	    } catch (Exception e) {
            log.error(e.toString(), e);
            data.put("infoMsg", "error");
			data.put("errMsg", e.toString());
            try {
				jsonString = mapper.writeValueAsString(data);
			} catch (JsonProcessingException e1) {
				//
			}
            return jsonString;
	    }

	    return jsonString;
	}
	
	/**
	 * 組態備份錯誤資料匯出
	 * @param model
	 * @param request
	 * @param response
	 * @param queryGroup
	 * @param queryDevice
	 * @param searchValue
	 * @param exportRecordCount
	 * @return
	 */
	@RequestMapping(value = "dataExport.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse dataExport(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
            @RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
            @RequestParam(name="exportDay", required=false, defaultValue="") String exportDay,
            @RequestParam(name="searchValue", required=false, defaultValue="") String searchValue,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    List<ProvisionLogConfigBackupError> dataList = new ArrayList<>();
	    VersionServiceVO vsVO;
        try {
            Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

            vsVO = new VersionServiceVO();
            if(StringUtils.isNotBlank(queryGroup)) {
            	setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice) ? "queryDevice" : "queryDeviceList", queryGroup, queryDevice);
            }            
            vsVO.setStartNum(queryStartNum);
            
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            
//            vsVO.setQueryDateEnd1(sdFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, -(Integer.valueOf(exportDay)));
            Date yesterday = calendar.getTime();
            vsVO.setQueryDateBegin1(sdFormat.format(yesterday));
            
            vsVO.setPageLength(queryPageLength);
            vsVO.setSearchValue(searchValue);
			
            dataList = provisionService.findProvisionLogConfigBackupError(vsVO);

            
            if (dataList != null && !dataList.isEmpty()) {
                String fileName = getFileName("Backup error detail_[CurrentTime]");
				String[] fieldNames = new String[] { "deviceId", "deviceName", "deviceIp", "deviceModel", "result",
						"message", "scriptCode", "remark", "beginTime", "endTime", "spendTimeInSeconds", "retryTimes", "processLog"
						};
				String[] columnsTitles = new String[] { "設備 ID", "設備名稱", "Ip", "型號", "執行結果", "訊息", "腳本代碼", "備註", "開始時間",
						"結束時間", "歷時(S)", "重試次數", "cmd紀錄" 
						};

                DataExportUtils export = new CsvExportUtils();
                String fileId = export.output2Web(response, fileName, true, dataList, fieldNames, columnsTitles);

                AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
                app.putData("fileId", fileId);
                return app;

            } else {
                AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "No matched data.");
                log.error("No matched data with queryStartNum = " + queryStartNum + ", queryPageLength = " + queryPageLength
    					+ ", date = " + vsVO.getQueryDateBegin1() + "~" + vsVO.getQueryDateEnd1() + ", queryPageLength = "
    					+ queryPageLength + ", searchValue = " + searchValue + ", queryGroup = "
    					+ queryGroup + ", queryDevice = " + queryDevice);
                return app;
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            return app;
        }
    }
}
