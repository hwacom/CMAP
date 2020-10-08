package com.cmap.plugin.module.provision;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.model.PrtgAccountMapping;
import com.cmap.security.SecurityUtil;
import com.cmap.service.ProvisionApiService;
import com.cmap.service.PrtgService;
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
		Map<String, String> data = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();		

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
			data = commonService.getScriptTypeMenu(null);

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
}
