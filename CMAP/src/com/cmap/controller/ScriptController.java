package com.cmap.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cmap.dao.vo.ScriptStepDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ScriptInfo;
import com.cmap.model.ScriptStepAction;
import com.cmap.model.ScriptType;
import com.cmap.security.SecurityUtil;
import com.cmap.service.ScriptService;
import com.cmap.service.vo.ScriptServiceVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Controller
@RequestMapping("/script")
public class ScriptController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_SCRIPT_TABLE_COLUMNS = new String[] {"","","si.scriptName","si.scriptType.scriptTypeCode","si.deviceModel","si.actionScript","si.actionScriptRemark","si.checkScript","si.checkScriptRemark","si.createTime","si.updateTime"};

	@Autowired
	private ScriptService scriptService;

	private void init(Model model, HttpServletRequest request) {
		model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
	}
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> scriptTypeMap = null;
		try {
			scriptTypeMap = getScriptTypeList(null);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("enableModify", Env.ENABLE_CM_SCRIPT_MODIFY);

			model.addAttribute("scriptType", "");
			model.addAttribute("scriptTypeList", scriptTypeMap);
			model.addAttribute("deviceModelList", getDeviceModelMap((String)request.getSession().getAttribute("PRTG_LOGIN_ACCOUNT")));
			
		}

		return "script/script_main";
	}

	/**
	 * 查找腳本資料
	 * @param model
	 * @param request
	 * @param response
	 * @param startNum
	 * @param pageLength
	 * @param queryScriptTypeCode
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getScriptData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse findScriptData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="queryScriptTypeCode", required=false, defaultValue="") String queryScriptTypeCode,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<ScriptServiceVO> dataList = new ArrayList<>();
		ScriptServiceVO ssVO;
		try {
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			
			ssVO = new ScriptServiceVO();
			ssVO.setStartNum(startNum);
			ssVO.setPageLength(pageLength);
			ssVO.setSearchValue(searchValue);
			ssVO.setOrderColumn(UI_SCRIPT_TABLE_COLUMNS[orderColIdx]);
			ssVO.setOrderDirection(orderDirection);
			if(StringUtils.isNotBlank(queryScriptTypeCode))ssVO.setQueryScriptTypeCode(Arrays.asList(queryScriptTypeCode));
			ssVO.setAdmin(isAdmin);
			
			filterdTotal = scriptService.countScriptInfo(ssVO);

			if (filterdTotal != 0) {
				dataList = scriptService.findScriptInfo(ssVO, startNum, pageLength);
			}

			filterdTotal = scriptService.countScriptInfo(new ScriptServiceVO());

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	@RequestMapping(value = "/view", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse viewConfig(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		final String commonErrorMsg = "預覽內容發生錯誤，請重新操作";
		try {
			String scriptInfoId = jsonData.findValue("scriptInfoId").asText();

			if (StringUtils.isBlank(scriptInfoId)) {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
			}

			ScriptServiceVO ssVO = scriptService.getScriptInfoByScriptInfoId(scriptInfoId);

			if (ssVO != null) {
				String viewType = jsonData.findValue("type").asText();

				Map<String, Object> retMap = new HashMap<>();
				retMap.put("scriptName", ssVO.getScriptName());

				if (StringUtils.equals(viewType, "A")) {
					retMap.put("content", ssVO.getActionScript());

				} else if (StringUtils.equals(viewType, "C")) {
					retMap.put("content", ssVO.getCheckScript());
				}

				retMap.put("scriptCode", ssVO.getScriptCode());
				retMap.put("remark", ssVO.getRemark());
				retMap.put("model", ssVO.getDeviceModel());
				retMap.put("type", String.join(", ", ssVO.getQueryScriptTypeCode()));
				retMap.put("systemDefault", ssVO.getScriptDefault());
				
				return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

			} else {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);
			}

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);
		}
	}
	

	@RequestMapping(value = "getScriptType.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse getScriptType(Model model, HttpServletRequest request, HttpServletResponse response) {

		ScriptStepDAOVO vo = new ScriptStepDAOVO();
		try {
			Map<String, String> scriptTypeMap = null;
			scriptTypeMap = getScriptTypeList(null);
			
			ObjectMapper oMapper = new ObjectMapper();
			vo.setScriptTypeName(new Gson().toJson(scriptTypeMap));
			Map<String, Object> retMap = oMapper.convertValue(vo, Map.class);
			
			return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
		}
	}
	

	@RequestMapping(value = "checkScriptCode.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse checkScriptCode(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		final String commonErrorMsg = "腳本代碼重複或發生錯誤，請重新操作";
		try {
			String scriptType = jsonData.findValue("scriptType").asText();

			if (StringUtils.isBlank(scriptType)) {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
			}

			List<ScriptInfo> ssVO = scriptService.getScriptInfoByScriptTypeCode(scriptType, null);
			
			int maxidx = 1;
			for(ScriptInfo info : ssVO) {
				if(maxidx < Integer.valueOf(info.getScriptCode().replace(scriptType, ""))) {
					maxidx = Integer.valueOf(info.getScriptCode().replace(scriptType, ""));
				}
			}
			String count = ssVO == null? "001": String.format("%03d", maxidx+1);
			
			return new AppResponse(HttpServletResponse.SC_OK, scriptType.concat(count));

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);
		}
	}
	

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public @ResponseBody AppResponse deleteScriptInfo(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			String scriptInfoId = jsonData.findValue("scriptInfoId").asText();

			String retMsg = scriptService.deleteScriptInfoByIdOrCode(scriptInfoId, null);

			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public @ResponseBody AppResponse saveScriptInfo(
			Model model, HttpServletRequest request, HttpServletResponse response, @RequestBody JsonNode jsonData) {

		try {			
			String scriptType = jsonData.findValue("addScriptType").asText();
			String scriptCode = jsonData.findValue("addScriptCode").asText();
			String scriptName = jsonData.findValue("addScriptName").asText();
			String deviceModel = jsonData.findValue("addDeviceModel").asText();
			String scriptContent = jsonData.findValue("addScriptContentValue").asText();
			String scriptRemark = jsonData.findValue("addScriptRemark").asText();
			String terminalSymbol = jsonData.findValue("terminalSymbol").asText();
			String errorSymbol = jsonData.findValue("errorSymbol").asText();

			com.cmap.model.ScriptType type = scriptService.getScriptTypeByCode(scriptType);
			
			ScriptInfo info = new ScriptInfo();
			info.setScriptType(type);
			info.setSystemDefault(Constants.DATA_N);
			info.setScriptCode(scriptCode);
			info.setScriptName(scriptName);
			info.setDeviceModel(deviceModel);
			info.setActionScript(scriptContent.replace(",", System.getProperty("line.separator")));
			info.setActionScriptRemark(scriptRemark);
			info.setAdminOnly(Constants.DATA_N);
			
			String[] scriptActions = StringUtils.split(scriptContent, ",");
			String[] terminalSymbols = StringUtils.splitPreserveAllTokens(terminalSymbol, ",");
			String[] errorSymbols = StringUtils.isBlank(errorSymbol) ? null :StringUtils.splitPreserveAllTokens(errorSymbol, ",");
			int index = 0;
			List<ScriptStepAction> scriptStepActions = new ArrayList<>();
			StringBuffer actionScriptVar = new StringBuffer("[\"");
			
			for(String scriptAction : scriptActions) {
				ScriptStepAction action = new ScriptStepAction();
				action.setStepOrder(index+1);
				action.setCommand(scriptAction);
				action.setExpectedTerminalSymbol(StringUtils.isBlank(terminalSymbols[index])?null:terminalSymbols[index]);
				action.setErrorSymbol(errorSymbols == null ?null:errorSymbols[index]);
				
				if(StringUtils.isBlank(terminalSymbols[index])) {
					action.setCommandRemark(Constants.SCRIPT_REMARK_OF_NO_EXPECT);
				}
				
				scriptStepActions.add(action);
				index++;
				
				if(scriptAction.indexOf("%") >= 0 ) {
					String[] array = scriptAction.split("%");
					for(int idx = 0; idx < array.length ; idx++) {
						if(idx % 2 == 1) {
							if(actionScriptVar.length() > 2) {
								actionScriptVar.append("\",\"");
							}
							actionScriptVar.append(array[idx].trim());
						}
					}
				}
			}
			
			info.setActionScriptVariable(actionScriptVar.append("\"]").toString());
			info.setScriptStepActions(scriptStepActions);
			
			String retMsg =scriptService.addOrModifyScriptInfo(info);
			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}
	
	private Map<String, String> getDeviceModelMap(String userName) {
		return commonService.getDeviceModelMap(userName);
	}
	
	@RequestMapping(value = "saveType", method = RequestMethod.POST)
	public @ResponseBody AppResponse saveScriptType(
			Model model, HttpServletRequest request, HttpServletResponse response, @RequestBody JsonNode jsonData) {

		try {
			String scriptTypeCode = jsonData.findValue("scriptTypeCode").asText();
			String scriptTypeName = jsonData.findValue("scriptTypeName").asText();

			com.cmap.model.ScriptType type = new ScriptType();
			
			type.setScriptTypeCode(scriptTypeCode);
			type.setScriptTypeName(scriptTypeName);
			
			String retMsg =scriptService.addOrModifyScriptType(type);
			
			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}
}
