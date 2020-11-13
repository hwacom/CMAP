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
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeviceService;
import com.cmap.service.vo.DeviceLoginInfoServiceVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.org.apache.xml.internal.security.utils.Base64;

@Controller
@RequestMapping("/deviceLoginInfo")
public class AdminDeviceLoginInfoController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_TABLE_COLUMNS = new String[] {"","","remark","connectionMode","loginAccount","loginPassword","enablePassword","enableBackup","communityString","udpPort","updateTime","updateBy"};

	@Autowired
	DeviceService deviceService;

	private void init(Model model, HttpServletRequest request) {
		model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
	}

	@RequestMapping(value = "main", method = RequestMethod.GET)
	public String adminEnv(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		Map<String, String> groupListMap = null;
		Map<String, String> deviceListMap = null;
		
		try {
			groupListMap = getGroupList(request);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("queryGroup", "");
			model.addAttribute("groupList", groupListMap);
			model.addAttribute("queryDevice", "");
			model.addAttribute("deviceList", deviceListMap);
			
			init(model, request);
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			model.addAttribute("userGroupList", getUserRightGroup(isAdmin?null:SecurityUtil.getSecurityUser().getUser().getUserUnit()));
			
			Map<String, String> enableBackupList = new HashMap<String, String>();
			Map<String, String> connectModeList = new HashMap<String, String>();
			
			connectModeList.put(Constants.SSH, Constants.SSH);
			connectModeList.put(Constants.TELNET, Constants.TELNET);

			enableBackupList.put(Constants.DATA_Y, Constants.DATA_Y);
			enableBackupList.put(Constants.DATA_N, Constants.DATA_N);
			
			model.addAttribute("enableBackupList", enableBackupList);
			model.addAttribute("connectModeList", connectModeList);
		}

		return "admin/admin_device_login_info";
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

			boolean retMsg = deviceService.deleteDeviceLoginInfo(ids);

			return new AppResponse(retMsg?HttpServletResponse.SC_OK:HttpServletResponse.SC_EXPECTATION_FAILED, retMsg?"刪除成功":"刪除失敗");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}

	@RequestMapping(value = "save", method = RequestMethod.POST)
	public @ResponseBody AppResponse modifyInfo(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();
			String modifyConnectionMode = jsonData.findValue("modifyConnectionMode").get(0).asText();
			String modifyLoginAccount = jsonData.findValue("modifyLoginAccount").get(0).asText();
			String modifyLoginPassword = jsonData.findValue("modifyLoginPassword").get(0).asText();
			String modifyEnablePassword = jsonData.findValue("modifyEnablePassword").get(0).asText();
			String modifyEnableBackup = jsonData.findValue("modifyEnableBackup").get(0).asText();
			List<String> ids = new ArrayList<>();
			while (idIt.hasNext()) {
				ids.add(idIt.next().asText());
			}
			
			DeviceLoginInfoServiceVO dliVO = new DeviceLoginInfoServiceVO();
			dliVO.setModifyConnectionMode(modifyConnectionMode);
			dliVO.setModifyLoginAccount(Base64.encode(modifyLoginAccount.getBytes()));
			dliVO.setModifyLoginPassword(Base64.encode(modifyLoginPassword.getBytes()));
			dliVO.setModifyEnablePassword(Base64.encode(modifyEnablePassword.getBytes()));
			dliVO.setModifyEnableBackup(modifyEnableBackup);

			String retMsg =deviceService.updateDeviceLoginInfo(ids, dliVO);
			return new AppResponse(HttpServletResponse.SC_OK, retMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			init(model, request);
		}
	}

	@RequestMapping(value = "getDeviceLoginInfo", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getDeviceLoginInfo(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="") String orderDirection) {

		long total = 0;
		long count = 0;
		String msg = null;
		List<DeviceLoginInfoServiceVO> dataList = new ArrayList<>();
		DeviceLoginInfoServiceVO dliVO;
		try {
			dliVO = new DeviceLoginInfoServiceVO();
			
			boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
			
			if(!isAdmin) return null;
			
			if(StringUtils.isNotBlank(queryDevice)) {
				setQueryDeviceList(request, dliVO, "queryDevice", queryGroup, queryDevice);
			}else {
				setQueryGroupList(request, dliVO, StringUtils.isNotBlank(queryGroup) ? "queryGroup" : "queryGroupList", queryGroup);
			}
			
			if (StringUtils.isNotBlank(searchValue)) {
				dliVO.setSearchValue(searchValue);
			}
			if (orderColIdx != null) {
				dliVO.setOrderColumn(UI_TABLE_COLUMNS[orderColIdx]);
				dliVO.setOrderDirection(orderDirection);
			}
			if(pageLength > 0) {
				dliVO.setStartNum(startNum);
				dliVO.setPageLength(pageLength);
			}
			
			total = deviceService.countDeviceLoginInfoList(dliVO);
			if(total > 0) {
				List<DeviceLoginInfo> list = deviceService.findDeviceLoginInfoList(dliVO);
				
				dataList = deviceService.transModel2VO(list);
			}

			count = dataList.size();
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			init(model, request);
		}

		return new DatatableResponse(total, dataList, count, msg);
	}

}
