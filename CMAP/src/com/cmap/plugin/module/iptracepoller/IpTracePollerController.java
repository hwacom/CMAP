package com.cmap.plugin.module.iptracepoller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.security.SecurityUtil;

@Controller
@RequestMapping("/plugin/module/ipTracePoller")
public class IpTracePollerController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private DatabaseMessageSourceBase messageSource;
	
	@Autowired
	private IpTracePollerService ipTracePollerService;
	
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

			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
		}
	}


	@RequestMapping(value = "", method = RequestMethod.GET)
	public String ipTracePoller(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_ip_trace_poller";
	}

	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
    		@RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientMac", required=false, defaultValue="") String queryClientMac,
			@RequestParam(name="queryClientIp", required=false, defaultValue="") String queryClientIp){

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    IpTracePollerVO searchVO;
        try {
        	searchVO = new IpTracePollerVO();
        	searchVO.setQueryGroupId(queryGroupId);
        	searchVO.setQueryDateBegin(queryDateBegin);
        	searchVO.setQueryDateEnd(queryDateEnd);
        	searchVO.setQueryTimeBegin(queryTimeBegin);
        	searchVO.setQueryTimeEnd(queryTimeEnd);
        	searchVO.setQueryClientMac(queryClientMac);
        	searchVO.setQueryClientIp(queryClientIp);
        	
	        filteredTotal = this.ipTracePollerService.countIpTraceDataFromDB(searchVO);
            retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        }
	}
	
	@RequestMapping(value = "getIpTraceData.json", method = {RequestMethod.POST, RequestMethod.GET})
	public @ResponseBody DatatableResponse getIpTraceData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroupId", required=true, defaultValue="") String queryGroupId,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="queryClientMac", required=false, defaultValue="") String queryClientMac,
			@RequestParam(name="queryClientIp", required=false, defaultValue="") String queryClientIp,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		String totalFlow = "";
		List<IpTracePollerVO> dataList = new ArrayList<>();
		try {
		    /*
			if (StringUtils.isBlank(queryGroupId)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("group.name", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<IpTracePollerVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<IpTracePollerVO>(), new Long(0), msg);
	        }
	        */

	        IpTracePollerVO resultVO = doDataQuery( queryGroupId, queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryClientMac,
	        		queryClientIp, startNum, pageLength,  orderColIdx, orderDirection );
	        
	        filteredTotal = resultVO.getMatchedList().size();
            dataList = resultVO.getMatchedList();
            total = resultVO.getTotalCount();

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, totalFlow);
	}
	
	
	private IpTracePollerVO doDataQuery( String queryGroupId, String queryDateBegin, String queryDateEnd, String queryTimeBegin, String queryTimeEnd, String queryClientMac,
			String queryClientIp, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws ServiceLayerException {

			IpTracePollerVO retVO = new IpTracePollerVO();
		    try {
		        	IpTracePollerVO searchVO = new IpTracePollerVO();
		        	searchVO.setQueryGroupId(queryGroupId);
		        	searchVO.setQueryDateBegin(queryDateBegin);
		        	searchVO.setQueryDateEnd(queryDateEnd);
		        	searchVO.setQueryTimeBegin(queryTimeBegin);
		        	searchVO.setQueryTimeEnd(queryTimeEnd);
		        	searchVO.setQueryClientMac(queryClientMac);
		        	searchVO.setQueryClientIp(queryClientIp);
		        	searchVO.setStartNum(startNum);
		        	searchVO.setPageLength(pageLength);
		        	searchVO.setOrderColumn(orderColIdx.toString()); // DAO SQL用別名同orderColIdx即可
		        	searchVO.setOrderDirection(orderDirection);
			        
		            List<IpTracePollerVO> dataList = new ArrayList<>();
		            dataList = this.ipTracePollerService.findModuleIpTrace(searchVO, startNum, pageLength);
		            retVO.setMatchedList(dataList);
		            
		    } catch (ServiceLayerException sle) {
	        } catch (Exception e) {
	            log.error(e.toString(), e);
	        }
	        return retVO;
		}
}
