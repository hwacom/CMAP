package com.cmap.plugin.module.inventory.info;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.plugin.module.alarm.summary.AlarmSummaryVO;
import com.cmap.security.SecurityUtil;
import com.cmap.service.vo.InventoryInfoVO;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Controller
@RequestMapping("/plugin/module/inventory")
public class InventoryController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private InventoryInfoService inventoryInfoService;

	@Autowired
	private DatabaseMessageSourceBase messageSource;
	
	/**
	 * 初始化選單
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
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
			
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			model.addAttribute("inputInvStatus", getMenuItem("INV_STATUS", false));
			model.addAttribute("inputInvUsers", getMenuItem("INV_USERS", false));
			model.addAttribute("inputInvDepartment", getMenuItem("INV_DEP", false));
			model.addAttribute("inputInvDeviceType", getMenuItem("INV_DEVICE_TYPE", false));
			
			model.addAttribute("selectionclassUse", getMenuItem("INV_DEP", false));
			model.addAttribute("selectionidcId", getMenuItem("INV_DEP", false));
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
		return "plugin/module_inventory";
	}

	@RequestMapping(value="addModify", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public AppResponse saveInventory(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		InventoryInfoVO iiVO = new InventoryInfoVO();
		try {
			convertJson2POJO(iiVO, jsonData);

			String retMag = inventoryInfoService.updateOrInsertInventoryInfo(Arrays.asList(iiVO));
			return new AppResponse(HttpServletResponse.SC_OK, retMag);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}	

	@RequestMapping(value = "delete", method = RequestMethod.POST)
	public @ResponseBody AppResponse deleteInv(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();
			String deleteRsn = jsonData.has("deleteRsn") ? jsonData.findValue("deleteRsn").asText() : null;
			
			List<String> ids = new ArrayList<>();
			while (idIt.hasNext()) {
				ids.add(idIt.next().asText());
			}

			boolean result = inventoryInfoService.deleteInventoryInfo(ids, deleteRsn);

			return result ? new AppResponse(HttpServletResponse.SC_OK, "刪除成功"):new AppResponse(HttpServletResponse.SC_OK, "刪除失敗");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "saveDetail", method = RequestMethod.POST)
	public @ResponseBody AppResponse saveDetail(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			String retMag = inventoryInfoService.updateInventoryDetail(jsonData);
			return new AppResponse(HttpServletResponse.SC_OK, retMag);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "getInventoryInfoData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getInventoryInfoData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="queryProbe", required=false, defaultValue="") String queryProbe,
			@RequestParam(name="queryDeviceName", required=false, defaultValue="") String queryDeviceName,
			@RequestParam(name="queryDeviceType", required=false, defaultValue="") String queryDeviceType,
			@RequestParam(name="queryDiffOnly", required=false, defaultValue="false") boolean queryDiffOnly,
			@RequestParam(name="queryIP", required=false, defaultValue="") String queryIP,
			@RequestParam(name="queryModel", required=false, defaultValue="") String queryModel,
			@RequestParam(name="queryGroupName", required=false, defaultValue="") String queryGroupName,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<InventoryInfoVO> dataList = new ArrayList<>();
	    try {

			dataList = doDataQuery(request, queryGroup, queryDevice, queryProbe, queryDeviceName, queryDeviceType,
					queryDiffOnly, queryIP, queryModel, queryGroupName, startNum, pageLength, orderColIdx,
					orderDirection);
	        filteredTotal = dataList.size();
	        total = dataList.size();
	        
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filteredTotal);
	}

	private List<InventoryInfoVO> doDataQuery(HttpServletRequest request, String queryGroup, String queryDevice,
			String queryProbe, String queryDeviceName, String queryDeviceType, boolean queryDiffOnly,
			String queryIP, String queryModel, String queryGroupName, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws Exception {

		InventoryInfoVO iiVO = new InventoryInfoVO();
		String[] fieldNames = new String[] { "", "", "deviceId", "probe", "groupName", "deviceName", "deviceIp", "deviceType",
				"brand", "model", "systemVersion", "serialNumber", "manufactureDate", "status", "syncFlag",
				"diffrenceComparison", "uploadTimeStr", "custodian", "department", "user", "northFlag", "remark" };
		
		try {
			if (StringUtils.isNotBlank(queryGroupName)) {
				iiVO.setQueryGroupName(queryGroupName);
			} else if (StringUtils.isNotBlank(queryDevice) || StringUtils.isNotBlank(queryGroup)) {
				setQueryDeviceList(request, iiVO, StringUtils.isNotBlank(queryDevice) ? "queryDevice":"queryDeviceList", queryGroup, queryDevice);
			}

			iiVO.setQueryProbe(queryProbe);
			iiVO.setQueryDeviceName(queryDeviceName);
			iiVO.setQueryDeviceType(queryDeviceType);
			iiVO.setQueryIP(queryIP);
			iiVO.setQueryModel(queryModel);
			iiVO.setStartNum(startNum);
			iiVO.setPageLength(pageLength);
			iiVO.setOrderDirection(orderDirection);
			iiVO.setQueryDiffOnly(queryDiffOnly);
			iiVO.setOrderColumn(fieldNames[orderColIdx]);
			iiVO.setOrderDirection(orderDirection);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 	inventoryInfoService.findInventoryInfo(iiVO);

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
	@RequestMapping(value = "dataExport.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse dataExport(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
            @RequestParam(name="queryProbe", required=false, defaultValue="") String queryProbe,
			@RequestParam(name="queryDeviceName", required=false, defaultValue="") String queryDeviceName,
			@RequestParam(name="queryDeviceType", required=false, defaultValue="") String queryDeviceType,
			@RequestParam(name="queryDiffOnly", required=false, defaultValue="false") boolean queryDiffOnly,
			@RequestParam(name="queryIP", required=false, defaultValue="") String queryIP,
			@RequestParam(name="queryModel", required=false, defaultValue="") String queryModel,
			@RequestParam(name="queryGroupName", required=false, defaultValue="") String queryGroupName,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    try {
	    	Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

	    	InventoryInfoVO iiVO = new InventoryInfoVO();
	    	if (StringUtils.isNotBlank(queryGroupName)) {
				iiVO.setQueryGroupName(queryGroupName);
			} else if (StringUtils.isNotBlank(queryDevice) || StringUtils.isNotBlank(queryGroup)) {
				setQueryDeviceList(request, iiVO, StringUtils.isNotBlank(queryDevice) ? "queryDevice":"queryDeviceList", queryGroup, queryDevice);
			}

			iiVO.setQueryProbe(queryProbe);
			iiVO.setQueryDeviceName(queryDeviceName);
			iiVO.setQueryIP(queryIP);
			iiVO.setQueryModel(queryModel);
			iiVO.setQueryDiffOnly(queryDiffOnly);
			iiVO.setStartNum(queryStartNum);
			iiVO.setPageLength(queryPageLength);
			
			List<Map<String, Object>> dataList = null;
			Map<String, String> typeMap = new HashMap<>();
			List<String> fileIdList = new ArrayList<>();
			
			if(StringUtils.isNotBlank(queryDeviceType)) {
				typeMap.put(queryDeviceType, queryDeviceType);
				
			} else {
				typeMap = getMenuItem("INV_DEVICE_TYPE", false);
			}
			
			//lamda forEach
//			typeMap.forEach((u, v) -> System.out.println("key:" + u + ",value:" + v));
			
			for (Map.Entry<String, String> entry : typeMap.entrySet()) {
//				System.out.println("key:" + entry.getKey() + ",value:" + entry.getValue());
				iiVO.setQueryDeviceType(entry.getKey());
				iiVO.setPageLength(queryPageLength);
				
				dataList = inventoryInfoService.findInvInfoAndDetailData(iiVO);
				queryPageLength = queryPageLength - dataList.size();
				
				if (dataList != null && !dataList.isEmpty()) {
		        	String fileName = getFileName("Inventory_Info_"+entry.getKey()+"_[CurrentTime]");
					
		            DataExportUtils export = new CsvExportUtils();
		            String fileId = export.output2Web(response, fileName, true, dataList, getExportFieldNames(entry.getKey()), getExportColumnTitles(entry.getKey()));
		            fileIdList.add(fileId);
				}
			}
			
			if(!fileIdList.isEmpty()) {
				AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
				app.putData("fileId", fileIdList);
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
			initMenu(model, request);
		}
	}
	
	private String[] getExportFieldNames(String deviceType) {
		String[] returnArray = new String[] { "deviceId", "probe", "groupName", "groupName1", "groupName2",
				"groupName3", "groupName4", "deviceName", "deviceIp", "deviceType", "brand", "model",
				"systemVersion", "serialNumber", "manufactureDate", "status", "syncFlag", "diffrenceComparison",
				"uploadTime", "custodian", "department", "user", "northFlag", "remark" };
				
		String[] detailFields = inventoryInfoService.getExportDetailFieldNames(deviceType);
		for(int i = 0 ; i < detailFields.length ; i ++) {
			String[] data = detailFields[i].split(Env.COMM_SEPARATE_SYMBOL);
			detailFields[i] = data[0];
		}
		return (String[])ArrayUtils.addAll(returnArray, detailFields);
	}
	
	private String[] getExportColumnTitles(String deviceType) {
		return (String[])ArrayUtils.addAll(new String[] { messageSource.getMessage("device.id", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.probe", Locale.TAIWAN, null),
				"第一層群組", "第二層群組", "第三層群組", "第四層群組", "第五層群組",
				messageSource.getMessage("ip.trace.poller.device.name", Locale.TAIWAN, null),
				messageSource.getMessage("ip.address", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.type", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.brand", Locale.TAIWAN, null),
				messageSource.getMessage("device.model", Locale.TAIWAN, null),
				messageSource.getMessage("system.version", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.serial.number", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.manufacture.date", Locale.TAIWAN, null),
				messageSource.getMessage("status", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.sync.flag", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.diffrence.comparison", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.upload.time", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.custodian", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.department", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.user", Locale.TAIWAN, null),
				messageSource.getMessage("func.plugin.inventory.north.flag", Locale.TAIWAN, null),
				messageSource.getMessage("remark", Locale.TAIWAN, null) }, inventoryInfoService.getExportDetailColumnTitles(deviceType));
	}
	
	@CrossOrigin(maxAge = 3600)
	@RequestMapping(value = "importData", method = RequestMethod.POST)
	public @ResponseBody AppResponse importData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectReader reader = objectMapper.readerFor(new TypeReference<List<InventoryInfoVO>>() {});
			List<InventoryInfoVO> dataList = reader.readValue(jsonData);
			
			String retMag = inventoryInfoService.insertInventoryInfo(dataList);
			return new AppResponse(HttpServletResponse.SC_OK, retMag);

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "getInvDetailData.json", method = RequestMethod.POST)
	public @ResponseBody AppResponse getInvDetailData(Model model, HttpServletRequest request,
			HttpServletResponse response, @RequestBody JsonNode jsonData) {
    		//查詢條件
		
			if(!jsonData.has("deviceId") || jsonData.findValues("deviceId").isEmpty()) {
				return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無明細資料!!");
			}
    		String deviceId = jsonData.findValues("deviceId").get(0).asText();
    		String deviceType = jsonData.findValues("deviceType").get(0).asText();

	    try {
	    	
	    	Map<String, Object> detail = inventoryInfoService.findInvDetailData(deviceId, deviceType);
	        
	    	if(detail == null ) {
	    		return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無明細資料!!");
	    	}
	    	
			return new AppResponse(HttpServletResponse.SC_OK, Env.COMM_SEPARATE_SYMBOL, detail);
			
	    } catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無明細資料!!");
		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
		} finally {
			initMenu(model, request);
		}
	}
}
