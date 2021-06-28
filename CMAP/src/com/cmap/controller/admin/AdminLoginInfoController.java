package com.cmap.controller.admin;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.security.SecurityUtil;
import com.cmap.service.SysLoginInfoService;
import com.cmap.service.vo.SysLoginInfoVO;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;

@Controller
@RequestMapping("/admin/loginInfo")
public class AdminLoginInfoController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private SysLoginInfoService sysLoginInfoService;

	@Autowired
	private DatabaseMessageSourceBase messageSource;


	//是否查詢條件為sensorId
//	private boolean isSensorSearchMode = StringUtils.equalsIgnoreCase(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR, Constants.DATA_Y);
	
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
		return "admin/admin_login_info";
	}

	@RequestMapping(value = "getLoginInfoData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getLoginInfoData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryUserAccount", required=false, defaultValue="") String queryUserAccount,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<SysLoginInfoVO> dataList = new ArrayList<>();
	    try {
	        if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<SysLoginInfoVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<SysLoginInfoVO>(), new Long(0), msg);
	        }

			dataList = doDataQuery(queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryUserAccount, startNum,
					pageLength, orderColIdx, orderDirection);
	        filteredTotal = dataList.size();
	        total = dataList.size();
	        
		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			behaviorLog(request);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, "");
	}

	private List<SysLoginInfoVO> doDataQuery(String queryDateBegin, String queryDateEnd, String queryTimeBegin,
			String queryTimeEnd, String queryUserAccount, Integer startNum, Integer pageLength, Integer orderColIdx,
			String orderDirection) throws ServiceLayerException {

		SysLoginInfoVO sliVO = new SysLoginInfoVO();
		sliVO.setQueryDateBegin(queryDateBegin);
		sliVO.setQueryDateEnd(queryDateEnd);
		sliVO.setQueryTimeBegin(queryTimeBegin);
		sliVO.setQueryTimeEnd(queryTimeEnd);
		sliVO.setQueryUserAccount(queryUserAccount);
		sliVO.setStartNum(startNum);
		sliVO.setPageLength(pageLength);
		// sliVO.setOrderColumn("From_Date_Time");
		sliVO.setOrderDirection(orderDirection);

		return sysLoginInfoService.findLoginInfo(sliVO);

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
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryUserAccount", required=false, defaultValue="") String queryUserAccount,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    try {
	        Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

			List<SysLoginInfoVO> dataList = doDataQuery(queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd,
					queryUserAccount, queryStartNum, queryPageLength, orderColIdx, orderDirection);

	        if (dataList != null && !dataList.isEmpty()) {
				String fileName = getFileName("LOGIN_INFO_[CurrentTime]");
				String[] fieldNames = new String[] { "sessionId", "ipAddr", "account", "userName", "loginTimeStr", "logoutTimeStr" };
				String[] columnsTitles = new String[] { "session id", "ip", "帳號", "使用者名稱", "登入時間", "登出時間" };

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
