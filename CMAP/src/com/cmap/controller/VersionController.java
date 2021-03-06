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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.cmap.comm.enums.RestoreMethod;
import com.cmap.exception.ServiceLayerException;
import com.cmap.security.SecurityUtil;
import com.cmap.service.VersionService;
import com.cmap.service.vo.VersionServiceVO;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/version")
public class VersionController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_MANAGE_TABLE_COLUMNS = new String[] {"","","groupName","deviceName","deviceModel","configType", "configVersion","createTime"};
	private static final String[] UI_BACKUP_TABLE_COLUMNS = new String[] {"","","dl.groupName","dl.deviceName","dl.deviceModel","cvi.configVersion","cvi.configType","cvi.createTime"};

	@Autowired
	private VersionService versionService;

	/**
	 * 初始化選單
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
		Map<String, String> groupListMap = null;
		Map<String, String> deviceListMap = null;
		Map<String, String> configTypeMap = null;
		try {
			groupListMap = getGroupList(request);
			configTypeMap = getMenuItem(Env.MENU_CODE_OF_CONFIG_TYPE, true);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("queryGroup1", "");
			model.addAttribute("group1List", groupListMap);
			model.addAttribute("queryDevice1", "");
			model.addAttribute("device1List", deviceListMap);

			//版本管理使用
			model.addAttribute("queryGroup2", "");
			model.addAttribute("group2List", groupListMap);
			model.addAttribute("queryDevice2", "");
			model.addAttribute("device2List", deviceListMap);
			
			model.addAttribute("queryConfigType", "");
			model.addAttribute("configTypeList", configTypeMap);

			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			
			behaviorLog(request);
		}
	}

	/**
	 * [版本管理] >> 主頁面
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/manage", method = RequestMethod.GET)
	public String manageMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "version/version_main";
	}

	/**
	 * [版本管理] >> 查看組態檔內容
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value = "/view", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse viewConfig(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		final String commonErrorMsg = "預覽內容發生錯誤，請重新操作";
		try {
			List<JsonNode> vIdList = jsonData.findValues("value");

			if (vIdList == null || (vIdList != null && vIdList.isEmpty())) {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
			}

			String versionId = vIdList.get(0).asText();

			List<String> versionIDs = new ArrayList<>();
			versionIDs.add(versionId);

			List<VersionServiceVO> vsVOList = versionService.findConfigFilesInfo(versionIDs);

			if (vsVOList != null && !vsVOList.isEmpty()) {
				VersionServiceVO retVO = versionService.getConfigFileContent(vsVOList.get(0), true);

				if (StringUtils.isNotBlank(retVO.getConfigFileContent())) {
					Map<String, Object> retMap = new HashMap<>();
					retMap.put("group", retVO.getGroupName());
					retMap.put("device", retVO.getDeviceName());
					retMap.put("version", retVO.getConfigVersion());
					retMap.put("content", retVO.getConfigFileContent());

					return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

				} else {
					log.error("configFileContent is blank ! "+versionIDs);
					return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);
				}
			} else {
				log.error("config not fund ! "+versionIDs);
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);
			}

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, commonErrorMsg);

		} finally {
			behaviorLog(request);
		}
	}

	/**
	 * [版本管理] >> 版本比對
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value = "/compare", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse compareFiles(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			List<JsonNode> vIdList = jsonData.findValues("value");

			if (vIdList == null || (vIdList != null && vIdList.isEmpty())) {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "版本比對只允許兩份檔案，請重新選擇");
			} else {
				if (vIdList.size() != 2) {
					return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "版本比對只允許兩份檔案，請重新選擇");
				}
			}

			List<String> versionIDs = new ArrayList<>();
			for (JsonNode node : vIdList) {
				versionIDs.add(node.asText());
			}

			List<VersionServiceVO> vsVOList = versionService.findConfigFilesInfo(versionIDs);

			for (VersionServiceVO vsVO : vsVOList) {
				vsVO.setCheckEnableCurrentDateSetting(true);
			}

			if (vsVOList != null && !vsVOList.isEmpty() && vsVOList.size() == 2) {
				VersionServiceVO retVO = versionService.compareConfigFiles(vsVOList);

				Map<String, Object> retMap = new HashMap<>();
				retMap.put("versionLeft", retVO.getVersionOri());
				retMap.put("versionRight", retVO.getVersionRev());
				retMap.put("versionLineNum", retVO.getVersionLineNum());
				retMap.put("contentLeft", retVO.getConfigDiffOriContent());
				retMap.put("contentRight", retVO.getConfigDiffRevContent());
				retMap.put("diffPos", retVO.getDiffPos());

				return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

			} else {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
			}


		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

		} finally {
			behaviorLog(request);
		}
	}

	/**
     * [版本管理] >> 版本比對結果查看
     * @param model
     * @param principal
     * @param request
     * @param response
     * @param jsonData
     * @return
     */
    @RequestMapping(value = "/diff/view/{diffLogId}", method = RequestMethod.GET)
    public String viewCompareResult(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
            @PathVariable String diffLogId) {

        try {
            VersionServiceVO retVO = versionService.viewCompareResult(diffLogId);

            if (retVO != null) {
                model.addAttribute("versionLeft", retVO.getVersionOri());
                model.addAttribute("versionRight", retVO.getVersionRev());
                model.addAttribute("versionLineNum", retVO.getVersionLineNum());
                model.addAttribute("contentLeft", retVO.getConfigDiffOriContent());
                model.addAttribute("contentRight", retVO.getConfigDiffRevContent());
                model.addAttribute("diffPos", retVO.getDiffPos());
            }

        } catch (ServiceLayerException sle) {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
        	behaviorLog(request);
        }

        return "version/version_diff_view";
    }

	/**
	 * [版本管理] >> 刪除組態檔
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse deleteFiles(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			List<String> versionIDs = new ArrayList<>();
			for (JsonNode jn : jsonData.findValues("value")) {
				versionIDs.add(jn.asText());
			}

			versionService.deleteVersionInfo(versionIDs);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			behaviorLog(request);
		}

		return new AppResponse(HttpServletResponse.SC_OK, "刪除成功");
	}

	private List<VersionServiceVO> doDataQuery(VersionServiceVO vsVO, Integer startNum, Integer pageLength) {
	    List<VersionServiceVO> retList = null;
	    try {
	        retList = versionService.findVersionInfo(vsVO, startNum, pageLength);

	    } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
	    return retList;
	}

	/**
	 * [版本管理] >> 查詢按鈕入口方法
	 * @param model
	 * @param request
	 * @param response
	 * @param startNum
	 * @param pageLength
	 * @param queryGroup1
	 * @param queryGroup2
	 * @param queryDevice1
	 * @param queryDevice2
	 * @param queryDateBegin1
	 * @param queryDateEnd1
	 * @param queryDateBegin2
	 * @param queryDateEnd2
	 * @param queryNewChkbox
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getVersionInfoData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse findVersionInfoData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="queryGroup1", required=false, defaultValue="") String queryGroup1,
			@RequestParam(name="queryGroup2", required=false, defaultValue="") String queryGroup2,
			@RequestParam(name="queryDevice1", required=false, defaultValue="") String queryDevice1,
			@RequestParam(name="queryDevice2", required=false, defaultValue="") String queryDevice2,
			@RequestParam(name="queryDateBegin1", required=false, defaultValue="") String queryDateBegin1,
			@RequestParam(name="queryDateEnd1", required=false, defaultValue="") String queryDateEnd1,
			@RequestParam(name="queryDateBegin2", required=false, defaultValue="") String queryDateBegin2,
			@RequestParam(name="queryDateEnd2", required=false, defaultValue="") String queryDateEnd2,
			@RequestParam(name="queryConfigType", required=false, defaultValue="") String queryConfigType,
			@RequestParam(name="queryNewChkbox", required=false, defaultValue="true") boolean queryNewChkbox,
			@RequestParam(name="maxCountByDevice", required=false, defaultValue="false") boolean maxCountByDevice,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<VersionServiceVO> dataList = new ArrayList<>();
		VersionServiceVO vsVO;
		try {
			vsVO = new VersionServiceVO();
			vsVO.setQueryConfigType(queryConfigType);
			
//			setQueryGroupList(request, vsVO, StringUtils.isNotBlank(queryGroup1) ? "queryGroup1" : "queryGroup1List", queryGroup1);
			setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice1) ? "queryDevice1" : "queryDevice1List", queryGroup1, queryDevice1);

			if(StringUtils.isNotBlank(queryGroup2)) {				
//				setQueryGroupList(request, vsVO, StringUtils.isNotBlank(queryGroup2) ? "queryGroup2" : "queryGroup2List", queryGroup2);
				setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice2) ? "queryDevice2" : "queryDevice2List", queryGroup2, queryDevice2);
			}
			
			//沒有時間條件則合併設備清單條件
			if (StringUtils.isBlank(queryDateBegin1) && StringUtils.isBlank(queryDateEnd1) 
					&& StringUtils.isBlank(queryDateBegin2) && StringUtils.isBlank(queryDateEnd2)) {
				List<String> deviceList = new ArrayList<>();
				
				if(StringUtils.isNotBlank(vsVO.getQueryDevice1())) {
					deviceList.add(vsVO.getQueryDevice1());
					vsVO.setQueryDevice1(null);
				}
				if(StringUtils.isNotBlank(vsVO.getQueryDevice2())) {
					deviceList.add(vsVO.getQueryDevice2());
					vsVO.setQueryDevice2(null);
				}
				if(vsVO.getQueryDevice1List() != null && !vsVO.getQueryDevice1List().isEmpty()) {
					deviceList.addAll(vsVO.getQueryDevice1List());
					vsVO.setQueryDevice1List(null);
				}
				if(vsVO.getQueryDevice2List() != null && !vsVO.getQueryDevice2List().isEmpty()) {
					deviceList.addAll(vsVO.getQueryDevice2List());
					vsVO.setQueryDevice2List(null);
				}
				
				vsVO.setQueryDevice1List(deviceList);
			}
			
			vsVO.setQueryNewChkbox(queryNewChkbox);
			if (!queryNewChkbox) {//包含舊版本
				vsVO.setQueryDateBegin1(queryDateBegin1);
				vsVO.setQueryDateEnd1(queryDateEnd1);
				vsVO.setQueryDateBegin2(queryDateBegin2);
				vsVO.setQueryDateEnd2(queryDateEnd2);
			}
			
			vsVO.setStartNum(startNum);
			vsVO.setPageLength(pageLength);
			vsVO.setSearchValue(searchValue);
			vsVO.setOrderColumn(UI_MANAGE_TABLE_COLUMNS[orderColIdx]);
			vsVO.setOrderDirection(orderDirection);

			/*
			 * 底下兩個參數用來後續查找使用者所有有權限的群組和設備筆數
			 */
//			setQueryGroupList(request, vsVO, "allGroupList", null);
//			setQueryDeviceList(request, vsVO, "allDeviceList", null, null);

			filterdTotal = versionService.countVersionInfo(vsVO);

			if (filterdTotal != 0) {
				dataList = doDataQuery(vsVO, startNum, pageLength);
			}

			if (!maxCountByDevice) {
				total = versionService.countUserPermissionAllVersionInfo(
						null, null, vsVO.getQueryConfigType());
			} else {
				total = vsVO.getAllDeviceList().size();
			}

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			behaviorLog(request);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	/**
	 *  [備份、還原] >> 查詢按鈕共用入口方法
	 * @param model
	 * @param request
	 * @param response
	 * @param startNum
	 * @param pageLength
	 * @param queryGroup
	 * @param queryDevice
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getDeviceListData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse findDeviceListData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<VersionServiceVO> dataList = new ArrayList<>();
		VersionServiceVO vsVO;
		try {
			vsVO = new VersionServiceVO();
//			setQueryGroupList(request, vsVO, StringUtils.isNotBlank(queryGroup) ? "queryGroup" : "queryGroupList", queryGroup);
			setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice) ? "queryDevice" : "queryDeviceList", queryGroup, queryDevice);
			
			vsVO.setStartNum(startNum);
			vsVO.setPageLength(pageLength);
			vsVO.setSearchValue(searchValue);
			vsVO.setOrderColumn(UI_BACKUP_TABLE_COLUMNS[orderColIdx]);
			vsVO.setOrderDirection(orderDirection);

			/*
			 * 底下兩個參數用來後續查找使用者所有有權限的群組和設備筆數
			 */
//			setQueryGroupList(request, vsVO, "allGroupList", null);
			setQueryDeviceList(request, vsVO, "allDeviceList", null, null);

			filterdTotal = versionService.countDeviceList(vsVO);
			
			if (filterdTotal != 0) {
				dataList = versionService.findDeviceList(vsVO, startNum, pageLength);
			}

			total = vsVO.getAllDeviceList().size();

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			behaviorLog(request);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	/**
	 * [版本備份] >> 主頁面
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/backup", method = RequestMethod.GET)
	public String backupMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "version/version_backup";
	}

	/**
	 * [版本備份] >> 執行備份動作
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value = "/backup/execute", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse doBackup(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		VersionServiceVO retVO;
		try {
			List<String> deviceIDs = new ArrayList<>();
			String configType = jsonData.findValue("configType") != null ? jsonData.findValue("configType").asText() : null;
			for (JsonNode jn : jsonData.findValues("deviceId")) {
				deviceIDs.add(jn.asText());
			}

			retVO = versionService.backupConfig(configType, deviceIDs, false, null);

			return new AppResponse(HttpServletResponse.SC_OK, retVO.getRetMsg());

		} catch (Exception e) {
			log.error(e.toString(), e);

			return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "備份失敗，請重新操作");

		} finally {
			behaviorLog(request);
		}
	}

	/**
	 * [版本還原] >> 主頁面
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/restore", method = RequestMethod.GET)
	public String recoverMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "version/version_restore";
	}

	@RequestMapping(value = "getVersionRecords.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse getVersionRecords(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="deviceListId", required=true) String deviceListId) {

		List<VersionServiceVO> retList;
		try {
			VersionServiceVO vsVO = new VersionServiceVO();
//			vsVO.setQueryDeviceListId(deviceListId);
			vsVO.setQueryDevice1List(Arrays.asList(deviceListId));
			retList = versionService.findVersionInfo(vsVO, null, null);

			AppResponse resp = new AppResponse(HttpServletResponse.SC_OK, "取得歷史版本紀錄成功");
			resp.putData("versionList", retList);
			return resp;

		} catch (Exception e) {
			log.error(e.toString(), e);

			return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "備份失敗，請重新操作");

		} finally {
			behaviorLog(request);
		}
	}

	/**
	 * [版本還原] >> 執行還原動作
	 * @param model
	 * @param principal
	 * @param request
	 * @param response
	 * @param jsonData
	 * @return
	 */
	@RequestMapping(value = "/restore/execute", method = RequestMethod.POST)
	public @ResponseBody AppResponse doRestore(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="deviceId", required=true) String deviceId,
			@RequestParam(name="versionId", required=true) String versionId) {

		VersionServiceVO retVO;
		try {
			VersionServiceVO vsVO = new VersionServiceVO();
			//vsVO.setDeviceListId("2c9e98c46850c32d016850c60c310003");		// NK-ePDG-HeNBGW-01
			//vsVO.setRestoreVersionId("40283a816893791101689380d893001c");	// 2019-01-29 16:08

			vsVO.setDeviceId(deviceId);
			vsVO.setRestoreVersionId(versionId);

			String userName = SecurityUtil.getSecurityUser().getUsername();
			String reason = "組態設定還原(設備ID:" + deviceId + ",版本ID:" + versionId + ")";
			retVO = versionService.restoreConfig(
					RestoreMethod.FTP, Constants.RESTORE_TYPE_BACKUP_RESTORE, vsVO, userName, reason);

			return new AppResponse(HttpServletResponse.SC_OK, retVO.getRetMsg());

		} catch (Exception e) {
			log.error(e.toString(), e);

			return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "還原失敗，請重新操作");

		} finally {
			behaviorLog(request);
		}
	}

	/**
	 * 資料匯出
	 * @param model
	 * @param request
	 * @param response
	 * @param queryGroup
	 * @param querySourceIp
	 * @param queryDestinationIp
	 * @param querySenderIp
	 * @param querySourcePort
	 * @param queryDestinationPort
	 * @param queryMac
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
	@RequestMapping(value = "dataExport.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse dataExport(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="queryGroup1", required=false, defaultValue="") String queryGroup1,
            @RequestParam(name="queryGroup2", required=false, defaultValue="") String queryGroup2,
            @RequestParam(name="queryDevice1", required=false, defaultValue="") String queryDevice1,
            @RequestParam(name="queryDevice2", required=false, defaultValue="") String queryDevice2,
            @RequestParam(name="queryDateBegin1", required=false, defaultValue="") String queryDateBegin1,
            @RequestParam(name="queryDateEnd1", required=false, defaultValue="") String queryDateEnd1,
            @RequestParam(name="queryDateBegin2", required=false, defaultValue="") String queryDateBegin2,
            @RequestParam(name="queryDateEnd2", required=false, defaultValue="") String queryDateEnd2,
            @RequestParam(name="queryConfigType", required=false, defaultValue="") String queryConfigType,
            @RequestParam(name="queryNewChkbox", required=false, defaultValue="true") boolean queryNewChkbox,
            @RequestParam(name="searchValue", required=false, defaultValue="") String searchValue,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    List<VersionServiceVO> dataList = new ArrayList<>();
	    VersionServiceVO vsVO;
        try {
            Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

            vsVO = new VersionServiceVO();
            setQueryGroupList(request, vsVO, StringUtils.isNotBlank(queryGroup1) ? "queryGroup1" : "queryGroup1List", queryGroup1);
            setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice1) ? "queryDevice1" : "queryDevice1List", queryGroup1, queryDevice1);

            vsVO.setQueryDateBegin1(queryDateBegin1);
            vsVO.setQueryDateEnd1(queryDateEnd1);
            setQueryGroupList(request, vsVO, StringUtils.isNotBlank(queryGroup2) ? "queryGroup2" : "queryGroup2List", queryGroup2);
            setQueryDeviceList(request, vsVO, StringUtils.isNotBlank(queryDevice2) ? "queryDevice2" : "queryDevice2List", queryGroup2, queryDevice2);

            vsVO.setQueryDateBegin2(queryDateBegin2);
            vsVO.setQueryDateEnd2(queryDateEnd2);
            vsVO.setQueryConfigType(queryConfigType);
            vsVO.setQueryNewChkbox(queryNewChkbox);
            vsVO.setStartNum(queryStartNum);
            vsVO.setPageLength(queryPageLength);
            vsVO.setSearchValue(searchValue);

            /*
             * 底下兩個參數用來後續查找使用者所有有權限的群組和設備筆數
             */
            setQueryGroupList(request, vsVO, "allGroupList", null);
            setQueryDeviceList(request, vsVO, "allDeviceList", null, null);

            dataList = doDataQuery(vsVO, queryStartNum, queryPageLength);

            if (dataList != null && !dataList.isEmpty()) {
                String fileName = getFileName(Env.EXPORT_DATA_CSV_FILE_NAME_OF_VERSION_MAIN);
                String[] fieldNames = new String[] {
                        "groupName", "deviceName", "deviceModel", "configVersion", "configType", "backupTimeStr"
                };
                String[] columnsTitles = Env.EXPORT_DATA_CSV_COLUMNS_TITLES_OF_VERSION_MAIN.split(",");

                DataExportUtils export = new CsvExportUtils();
                String fileId = export.output2Web(response, fileName, true, dataList, fieldNames, columnsTitles);

                AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
                app.putData("fileId", fileId);
                return app;

            } else {
                AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "No matched data.");
                return app;
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            return app;
        } finally {
			behaviorLog(request);
		}
    }
}
