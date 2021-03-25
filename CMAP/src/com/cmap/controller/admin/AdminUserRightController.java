package com.cmap.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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
import com.cmap.security.SecurityUtil;
import com.cmap.service.UserService;
import com.cmap.service.vo.UserRightServiceVO;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/userRight")
public class AdminUserRightController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_TABLE_COLUMNS = new String[] {"","","account","userName","","userGroupStr","","remark","loginMode","isAdmin","createTimeStr","createBy","updateTimeStr","updateBy"};

	@Autowired
	UserService userService;

	private void init(Model model, HttpServletRequest request) {
		model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
		behaviorLog(request.getRequestURI(), request.getQueryString());
	}

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String adminEnv(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {
			
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			init(model, request);
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			
			Map<String, String> retMap = new LinkedHashMap<>();
			Map<String, String> typeMap = getUserRightGroup(isAdmin?null:SecurityUtil.getSecurityUser().getUser().getUserUnit());
			/*
			 * 排序設定處理
			 */
			if (Env.SORT_GROUP_MENU_BY_GROUP_NAME_INCLUDED_SEQ_NO) {
				Map<Integer, String> sortedMap = new TreeMap<>();
				Map<String, String> sortedNonNumberMap = new TreeMap<>();
				for (Map.Entry<String, String> entry : typeMap.entrySet()) {
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
					retMap.put(sourceKey, typeMap.get(sourceKey));
				}
				for (String sourceKey : sortedNonNumberMap.values()) {
					retMap.put(sourceKey, typeMap.get(sourceKey));
				}

			} else {
				for (Map.Entry<String, String> entry : typeMap.entrySet()) {
					retMap.put(entry.getKey(), entry.getValue());
				}
			}
			model.addAttribute("userGroupList", retMap);
			
			Map<String, String> loginModeMap = new HashMap<String, String>();
			for(String mode : Env.LOGIN_MODE) {
				loginModeMap.put(mode, mode);
			}
			model.addAttribute("loginModeList", loginModeMap);
			model.addAttribute("checkPWDate", true);
		}

		return "admin/admin_user_right";
		
	}

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public @ResponseBody AppResponse deleteEnv(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();

			List<String> ids = new ArrayList<>();
			while (idIt.hasNext()) {
				ids.add(idIt.next().asText());
			}

			String retMsg = userService.deleteSettings(ids);

			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public @ResponseBody AppResponse modifyEnv(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			List<UserRightServiceVO> urVOs = new ArrayList<>();

			Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();
			Iterator<JsonNode> accountIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_ACCOUNT).get(0).iterator();
			Iterator<JsonNode> nameIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_USER_NAME).get(0).iterator();
			Iterator<JsonNode> passwordIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_PASSWORD).get(0).iterator();
			Iterator<JsonNode> groupIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_USERGROUP) != null ? jsonData.findValues(Constants.JSON_FIELD_MODIFY_USERGROUP).get(0).iterator():null;
			Iterator<JsonNode> isAdminIt = jsonData.findValues(Constants.JSON_FIELD_ADD_IS_ADMIN) != null ?jsonData.findValues(Constants.JSON_FIELD_ADD_IS_ADMIN).get(0).iterator() :null;
			Iterator<JsonNode> modeIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_LOGIN_MODE) != null ? jsonData.findValues(Constants.JSON_FIELD_MODIFY_LOGIN_MODE).get(0).iterator():null;
			Iterator<JsonNode> remarkIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_REMARK) != null ? jsonData.findValues(Constants.JSON_FIELD_MODIFY_REMARK).get(0).iterator():null;
			
			UserRightServiceVO urVO;
			while (nameIt.hasNext()) {
				urVO = new UserRightServiceVO();
				urVO.setId(idIt.hasNext()?idIt.next().asText():null);
				urVO.setAccount(accountIt.hasNext()?accountIt.next().asText():null);
				urVO.setUserName(nameIt.hasNext() ? nameIt.next().asText() : null);
				urVO.setPassword(passwordIt.hasNext() ? passwordIt.next().asText() : null);
				urVO.setUserGroup(groupIt != null && groupIt.hasNext() ? groupIt.next().asText() : null);
				urVO.setIsAdmin(isAdminIt != null && isAdminIt.hasNext() ? isAdminIt.next().asText() : null);
				urVO.setLoginMode(modeIt != null && modeIt.hasNext() ? modeIt.next().asText() : null);
				urVO.setRemark(remarkIt != null && remarkIt.hasNext() ? remarkIt.next().asText() : null);
				
				if(StringUtils.equals(urVO.getLoginMode(), Constants.LOGIN_AUTH_MODE_CM) || StringUtils.isBlank(urVO.getLoginMode())){
					List<String> errorList = isValid(urVO.getPassword());
					
					if(errorList.size() > 0) {
						return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, urVO.getAccount() + "輸入密碼違反密碼規則：<br>" + StringUtils.join(errorList, "<br>"));
					}
				}				
				urVOs.add(urVO);
			}

			String retMsg =userService.addOrModifyUserRightSettings(urVOs);
			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}

	private List isValid(String pw) {
	    Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
	    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
	    Pattern digitCasePatten = Pattern.compile("[0-9 ]");
	    List<String> errorList = new ArrayList<>();
	    
	    if(StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_FLAG)) {
	    	
		    if (Env.PASSWORD_VALID_SETTING_LENGTH != null && pw.length() < Integer.parseInt(Env.PASSWORD_VALID_SETTING_LENGTH)) {
		        errorList.add("密碼長度不可短於"+Env.PASSWORD_VALID_SETTING_LENGTH+"位數!!");
		    }
		    if (StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_CONTAIN_SPECAIL_CHAR) && !specailCharPatten.matcher(pw).find()) {
		        errorList.add("密碼必須包含特殊符號!!");
		    }
		    if (StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_CONTAIN_UPCASE) &&!UpperCasePatten.matcher(pw).find()) {
		        errorList.add("密碼必須包含大寫字母!!");
		    }
		    if (StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_CONTAIN_LOWERCASE) &&!lowerCasePatten.matcher(pw).find()) {
		        errorList.add("密碼必須包含小寫字母!!");
		    }
		    if (StringUtils.equalsIgnoreCase(Constants.DATA_Y, Env.PASSWORD_VALID_SETTING_CONTAIN_NUMBER) &&!digitCasePatten.matcher(pw).find()) {
		        errorList.add("密碼必須包含數字!!");
		    }
	    }
	    return errorList;
	}
	
	@RequestMapping(value = "getUserRightSetting", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getUserRightSetting(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="") String orderDirection,
			@RequestParam(name="account", required=false, defaultValue="") String account,
			@RequestParam(name="password", required=false, defaultValue="") String password,
			@RequestParam(name="userName", required=false, defaultValue="") String userName,
			@RequestParam(name="userGroup", required=false, defaultValue="") String userGroup) {

		long total = 0;
		long filterdTotal = 0;
		String msg = null;
		List<UserRightServiceVO> dataList = new ArrayList<>();
		UserRightServiceVO urVO;
		try {
			urVO = new UserRightServiceVO();
			
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			
			//非管理員權限只可查詢自己
			urVO.setAccount(isAdmin?null:SecurityUtil.getSecurityUser().getUser().getUserUnit());
			urVO.setUserName(userName);
			urVO.setUserGroup(userGroup);
			
			if (StringUtils.isNotBlank(searchValue)) {
				urVO.setSearchValue(searchValue);
			}
			if (orderColIdx != null) {
				urVO.setOrderColumn(UI_TABLE_COLUMNS[orderColIdx]);
				urVO.setOrderDirection(orderDirection);
			}

			filterdTotal = userService.countUserRightSettingsByVO(urVO);

			if (filterdTotal > 0) {
				dataList = userService.findUserRightSettingsByVO(urVO, null, null);
			}

			total = userService.countUserRightSettingsByVO(new UserRightServiceVO());

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			init(model, request);
		}

		return new DatatableResponse(total, dataList, filterdTotal, msg);
	}

}
