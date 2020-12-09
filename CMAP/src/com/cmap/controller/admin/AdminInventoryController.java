package com.cmap.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cmap.security.SecurityUtil;
import com.cmap.service.InventoryInfoService;
import com.cmap.service.vo.InventoryInfoVO;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private InventoryInfoService inventoryInfoService;

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
		}
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "admin/admin_inventory";
	}

	@RequestMapping(value="save", method = RequestMethod.POST, produces="application/json")
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
	public @ResponseBody AppResponse deleteEnv(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_IDS).get(0).iterator();

			List<String> ids = new ArrayList<>();
			while (idIt.hasNext()) {
				ids.add(idIt.next().asText());
			}

			boolean result = inventoryInfoService.deleteInventoryInfo(ids);

			return result ? new AppResponse(HttpServletResponse.SC_OK, "刪除成功"):new AppResponse(HttpServletResponse.SC_OK, "刪除失敗");

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
			@RequestParam(name="queryModifyOnly", required=false, defaultValue="false") boolean queryModifyOnly,
			@RequestParam(name="queryBrand", required=false, defaultValue="") String queryBrand,
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
					queryModifyOnly, queryBrand, queryModel, queryGroupName, startNum, pageLength, orderColIdx,
					orderDirection);
	        filteredTotal = dataList.size();
	        total = dataList.size();
	        
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal);
	}

	private List<InventoryInfoVO> doDataQuery(HttpServletRequest request, String queryGroup, String queryDevice,
			String queryProbe, String queryDeviceName, String queryDeviceType, boolean queryModifyOnly,
			String queryBrand, String queryModel, String queryGroupName, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws ServiceLayerException {

		InventoryInfoVO iiVO = new InventoryInfoVO();
		
		try {
			if (StringUtils.isNotBlank(queryGroupName)) {
				iiVO.setQueryGroupName(queryGroupName);
			} else if (StringUtils.isNotBlank(queryDevice) || StringUtils.isNotBlank(queryGroup)) {
				setQueryDeviceList(request, iiVO, StringUtils.isNotBlank(queryDevice) ? "queryDevice":"queryDeviceList", queryGroup, queryDevice);
			}

			iiVO.setQueryProbe(queryProbe);
			iiVO.setQueryDeviceName(queryDeviceName);
			iiVO.setQueryDeviceType(queryDeviceType);
			iiVO.setQueryBrand(queryBrand);
			iiVO.setQueryModel(queryModel);
			iiVO.setStartNum(startNum);
			iiVO.setPageLength(pageLength);
			iiVO.setOrderDirection(orderDirection);
			iiVO.setQueryModifyOnly(queryModifyOnly);
			
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
			@RequestParam(name="queryModifyOnly", required=false, defaultValue="false") boolean queryModifyOnly,
			@RequestParam(name="queryBrand", required=false, defaultValue="") String queryBrand,
			@RequestParam(name="queryModel", required=false, defaultValue="") String queryModel,
			@RequestParam(name="queryGroupName", required=false, defaultValue="") String queryGroupName,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    try {
	        Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

			List<InventoryInfoVO> dataList = doDataQuery(request, queryGroup, queryDevice, queryProbe, queryDeviceName,
					queryDeviceType, queryModifyOnly, queryBrand, queryModel, queryGroupName, startNum, pageLength,
					orderColIdx, orderDirection);

	        if (dataList != null && !dataList.isEmpty()) {
				String fileName = getFileName("Inventory_Info_[CurrentTime]");
				String[] fieldNames = new String[] { "deviceId", "probe", "group", "deviceName", "deviceIp",
						"deviceType", "brand", "model", "systemVersion", "serialNumber", "manufactureDate" };
				String[] columnsTitles = new String[] { "設備 ID", "所屬 Probe", "所屬群組", "設備名稱", "IP_Address", "設備類型",
						"設備廠牌", "設備型號", "軟體版本", "序號", "出廠日期" };

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
	    }
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
	
}
