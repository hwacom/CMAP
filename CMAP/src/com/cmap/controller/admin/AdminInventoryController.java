package com.cmap.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmap.AppResponse;
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
		try {
			
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
		}
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String netFlow(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "admin/admin_inventory";
	}

	@RequestMapping(value = "getInventoryInfoData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getInventoryInfoData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryProbe", required=false, defaultValue="") String queryProbe,
			@RequestParam(name="queryDeviceName", required=false, defaultValue="") String queryDeviceName,
			@RequestParam(name="queryDeviceType", required=false, defaultValue="") String queryDeviceType,
			@RequestParam(name="queryBrand", required=false, defaultValue="") String queryBrand,
			@RequestParam(name="queryModel", required=false, defaultValue="") String queryModel,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<InventoryInfoVO> dataList = new ArrayList<>();
	    try {

			dataList = doDataQuery(queryProbe, queryDeviceName, queryDeviceType, queryBrand, queryModel, startNum,
					pageLength, orderColIdx, orderDirection);
	        filteredTotal = dataList.size();
	        total = dataList.size();
	        
		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, "");
	}

	private List<InventoryInfoVO> doDataQuery(String queryProbe, String queryDeviceName, String queryDeviceType,
			String queryBrand, String queryModel, Integer startNum, Integer pageLength, Integer orderColIdx,
			String orderDirection) throws ServiceLayerException {

		InventoryInfoVO iiVO = new InventoryInfoVO();
		iiVO.setQueryProbe(queryProbe);
		iiVO.setQueryDeviceName(queryDeviceName);
		iiVO.setQueryDeviceType(queryDeviceType);
		iiVO.setQueryBrand(queryBrand);
		iiVO.setQueryModel(queryModel);
		iiVO.setStartNum(startNum);
		iiVO.setPageLength(pageLength);
		iiVO.setOrderDirection(orderDirection);

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
            @RequestParam(name="queryProbe", required=false, defaultValue="") String queryProbe,
			@RequestParam(name="queryDeviceName", required=false, defaultValue="") String queryDeviceName,
			@RequestParam(name="queryDeviceType", required=false, defaultValue="") String queryDeviceType,
			@RequestParam(name="queryBrand", required=false, defaultValue="") String queryBrand,
			@RequestParam(name="queryModel", required=false, defaultValue="") String queryModel,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    try {
	        Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

			List<InventoryInfoVO> dataList = doDataQuery(queryProbe, queryDeviceName, queryDeviceType, queryBrand,
					queryModel, queryStartNum, queryPageLength, orderColIdx, orderDirection);
			
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
}
