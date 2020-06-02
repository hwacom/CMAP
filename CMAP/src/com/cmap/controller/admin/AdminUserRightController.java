package com.cmap.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cmap.security.SecurityUtil;
import com.cmap.service.UserService;
import com.cmap.service.vo.UserRightServiceVO;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/userRight")
public class AdminUserRightController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_TABLE_COLUMNS = new String[] {"","","settingName","settingValue","settingRemark","createTime","createBy","updateTime","updateBy"};

	@Autowired
	UserService userService;

	private void init(Model model, HttpServletRequest request) {
		model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
	}

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String adminEnv(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			init(model, request);
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			com.cmap.model.User user = SecurityUtil.getSecurityUser().getUser();
			model.addAttribute("userGroupList", getUserRightGroup(isAdmin?null:SecurityUtil.getSecurityUser().getUser().getUserName()));
			
			Map<String, String> loginModeMap = new HashMap<String, String>();
			for(String mode : Env.LOGIN_MODE) {
				loginModeMap.put(mode, mode);
			}
			model.addAttribute("loginModeList", loginModeMap);
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

			Iterator<JsonNode> accountIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_ACCOUNT).get(0).iterator();
			Iterator<JsonNode> nameIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_USER_NAME).get(0).iterator();
			Iterator<JsonNode> passwordIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_PASSWORD).get(0).iterator();
			Iterator<JsonNode> groupIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_USERGROUP) != null ? jsonData.findValues(Constants.JSON_FIELD_MODIFY_USERGROUP).get(0).iterator():null;
			Iterator<JsonNode> isAdminIt = jsonData.findValues(Constants.JSON_FIELD_ADD_IS_ADMIN) != null ?jsonData.findValues(Constants.JSON_FIELD_ADD_IS_ADMIN).get(0).iterator() :null;
			Iterator<JsonNode> modeIt = jsonData.findValues(Constants.JSON_FIELD_MODIFY_LOGIN_MODE) != null ? jsonData.findValues(Constants.JSON_FIELD_MODIFY_LOGIN_MODE).get(0).iterator():null;
			
			UserRightServiceVO urVO;
			while (nameIt.hasNext()) {
				urVO = new UserRightServiceVO();
				urVO.setAccount(accountIt.hasNext()?accountIt.next().asText():null);
				urVO.setUserName(nameIt.hasNext() ? nameIt.next().asText() : null);
				urVO.setPassword(passwordIt.hasNext() ? passwordIt.next().asText() : null);
				urVO.setUserGroup(groupIt != null && groupIt.hasNext() ? groupIt.next().asText() : null);
				urVO.setIsAdmin(isAdminIt != null && isAdminIt.hasNext() ? isAdminIt.next().asText() : null);
				urVO.setLoginMode(modeIt != null && modeIt.hasNext() ? modeIt.next().asText() : null);
				
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

	@RequestMapping(value = "getEnvConfig.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse findDeviceListData(
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
			
			if(isAdmin) {
				urVO.setAccount(account);
			}else {//非管理員權限只可查詢自己
				urVO.setAccount((String)request.getSession().getAttribute(Constants.USERNAME));
			}
			
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
