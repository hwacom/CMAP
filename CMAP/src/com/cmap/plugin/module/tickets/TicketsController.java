package com.cmap.plugin.module.tickets;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import com.cmap.plugin.module.iptracepoller.IpTracePollerVO;
import com.cmap.security.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/plugin/module/tickets")
public class TicketsController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private TicketListService ticketListService;

	@Autowired
	private DatabaseMessageSourceBase messageSource;

	/**
	 * 初始化選單
	 * 
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("userAccount", SecurityUtil.getSecurityUser().getUser().getUserName());
			model.addAttribute("userGroup", SecurityUtil.getSecurityUser().getUser().getPrtgLoginAccount());
			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
			
			model.addAttribute("inputQueryStatus", getMenuItem("TICKET_STATUS", false));
			model.addAttribute("inputQueryGroup", getUserGroupList((boolean)request.getSession().getAttribute(Constants.ISADMIN)?null:SecurityUtil.getSecurityUser().getUser().getUserName()));
			model.addAttribute("inputQueryOwner", getUserRightList(null));
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
		return "plugin/module_tickets";
	}


	@RequestMapping(value = "getTotalFilteredCount.json", method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name = "queryDateBegin", required = true, defaultValue = "") String queryDateBegin,
			@RequestParam(name = "queryDateEnd", required = false, defaultValue = "") String queryDateEnd,
			@RequestParam(name = "queryTimeBegin", required = true, defaultValue = "") String queryTimeBegin,
			@RequestParam(name = "queryTimeEnd", required = false, defaultValue = "") String queryTimeEnd,
			@RequestParam(name = "queryOwner", required = false, defaultValue = "") String queryOwner,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus){

	    String retVal = "N/A";
	    long filteredTotal = 0;
	    IpTracePollerVO searchVO;
        try {
        	if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("date", Locale.TAIWAN, null);
				AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
	            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, msg);
	            return app;
			}
			if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("time", Locale.TAIWAN, null);
				AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
	            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, msg);
	            return app;
			}

			TicketListVO tlVO = new TicketListVO();
			tlVO.setQueryDateBegin(queryDateBegin);
			tlVO.setQueryDateEnd(queryDateEnd);
			tlVO.setQueryTimeBegin(queryTimeBegin);
			tlVO.setQueryTimeEnd(queryTimeEnd);
			if(StringUtils.isNoneBlank(queryOwner.substring(queryOwner.indexOf("-")+1))) {
				String isGroup = messageSource.getMessage("group.name", Locale.TAIWAN, null).equals(queryOwner.substring(0, queryOwner.indexOf("-")))?"G":"U";
				tlVO.setQueryOwnerType(isGroup);
				tlVO.setQueryOwner(queryOwner.substring(queryOwner.indexOf("-")+1));
			}
			tlVO.setQueryStatus(queryStatus);
			filteredTotal = ticketListService.countModuleTicketList(tlVO);
			
            retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;

        } catch (ServiceLayerException sle) {
        	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);

            AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_FILTERED_COUNT, retVal);
            return app;
        } finally {
			initMenu(model, request);
		}
	}
	
	@RequestMapping(value = "getTicketData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getTicketData(Model model, HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(name = "queryDateBegin", required = true, defaultValue = "") String queryDateBegin,
			@RequestParam(name = "queryDateEnd", required = false, defaultValue = "") String queryDateEnd,
			@RequestParam(name = "queryTimeBegin", required = true, defaultValue = "") String queryTimeBegin,
			@RequestParam(name = "queryTimeEnd", required = false, defaultValue = "") String queryTimeEnd,
			@RequestParam(name = "queryOwner", required = false, defaultValue = "") String queryOwner,
			@RequestParam(name = "queryStatus", required = false, defaultValue = "") String queryStatus,
			@RequestParam(name = "start", required = false, defaultValue = "0") Integer startNum,
			@RequestParam(name = "length", required = false, defaultValue = "100") Integer pageLength,
			@RequestParam(name = "order[0][column]", required = false, defaultValue = "0") Integer orderColIdx,
			@RequestParam(name = "order[0][dir]", required = false, defaultValue = "desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		List<TicketListVO> dataList = new ArrayList<>();
		try {
			if (StringUtils.isBlank(queryDateBegin) || StringUtils.isBlank(queryDateEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("date", Locale.TAIWAN, null);
				return new DatatableResponse(new Long(0), new ArrayList<TicketListVO>(), new Long(0), msg);
			}
			if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
				String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null)
						+ messageSource.getMessage("time", Locale.TAIWAN, null);
				return new DatatableResponse(new Long(0), new ArrayList<TicketListVO>(), new Long(0), msg);
			}

			dataList = doDataQuery(queryDateBegin, queryDateEnd, queryTimeBegin, queryTimeEnd, queryOwner, queryStatus,
					startNum, pageLength, orderColIdx, orderDirection);
			filteredTotal = dataList.size();
			total = dataList.size();

//			File dir = new File("");
//			Process process = Runtime.getRuntime().exec("", null, dir);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, "");
	}

	private List<TicketListVO> doDataQuery(String queryDateBegin, String queryDateEnd, String queryTimeBegin,
			String queryTimeEnd, String queryOwner, String queryStatus, Integer startNum, Integer pageLength,
			Integer orderColIdx, String orderDirection) throws ServiceLayerException {

		String[] orderCol = new String[]{"update_Time", "priority", "list_id", "subject", "owner", "status", "remark"};
	      
		TicketListVO tlVO = new TicketListVO();
		tlVO.setQueryDateBegin(queryDateBegin);
		tlVO.setQueryDateEnd(queryDateEnd);
		tlVO.setQueryTimeBegin(queryTimeBegin);
		tlVO.setQueryTimeEnd(queryTimeEnd);
		if(StringUtils.isNoneBlank(queryOwner.substring(queryOwner.indexOf("-")+1))) {
			String isGroup = messageSource.getMessage("group.name", Locale.TAIWAN, null).equals(queryOwner.substring(0, queryOwner.indexOf("-")))?"G":"U";
			tlVO.setQueryOwnerType(isGroup);
			tlVO.setQueryOwner(queryOwner.substring(queryOwner.indexOf("-")+1));
		}
		tlVO.setQueryStatus(queryStatus);
		tlVO.setStartNum(startNum);
		tlVO.setPageLength(pageLength);
		tlVO.setOrderColumn(orderCol[orderColIdx]);
		tlVO.setOrderDirection(orderDirection);

		return ticketListService.findModuleTicketList(tlVO, startNum, pageLength);

	}

	@RequestMapping(value = "getTicketDetail.json", method = RequestMethod.GET)
	public String getTicketDetail(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "queryListId", required = true) Long queryListId) {
		
		try {
			model.addAttribute("queryListId", queryListId);
			ModuleTicketList list = ticketListService.findTicketList(queryListId);
			List<ModuleTicketDetail> ticketDetail = ticketListService.findModuleTicketDetail(queryListId);

			ObjectMapper oMapper = new ObjectMapper();
			// object -> Map
			Map<String, Object> listMap = oMapper.convertValue(list, Map.class);
			if(StringUtils.equals("G", list.getOwnerType())) {
				listMap.put("ownerStr", getUserGroupList(list.getOwner()).get(list.getOwner()));
			}else {
				listMap.put("ownerStr", getUserRightList(list.getOwner()).get(list.getOwner()));
			}
			
			model.addAttribute("ticket", listMap);
			
			StringBuffer historyContent = new StringBuffer();
			for(ModuleTicketDetail detail : ticketDetail) {
				historyContent.append("<div class=\"entry text-with-hr\"><div class=\"modified\"><b><span>");
				historyContent.append(messageSource.getMessage("update.by", Locale.TAIWAN, null)).append("：").append(detail.getCreateBy()).append("&nbsp;&nbsp;&nbsp;");
				historyContent.append(messageSource.getMessage("func.plugin.ticket.owner", Locale.TAIWAN, null)).append("：").append(detail.getDetailOwner()).append("&nbsp;&nbsp;&nbsp;");
				historyContent.append(messageSource.getMessage("create.time", Locale.TAIWAN, null)).append("：").append(Constants.FORMAT_YYYYMMDD_HH24MISS.format(detail.getCreateTime())).append("&nbsp;&nbsp;&nbsp;");
				historyContent.append("</span> </b></div><div class=\"content\">");
				historyContent.append(detail.getContent());
				historyContent.append("</div></div><br/>");
			}
			model.addAttribute("historyContent", historyContent);
			
		} catch (ServiceLayerException sle) {
			model.addAttribute("errorMsg", sle.getMessage());
			return "plugin/module_tickets";
        } catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_ticket_detail";
	}
	
	@RequestMapping(value = "save", method = RequestMethod.POST)
	public @ResponseBody AppResponse saveDetail(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			ModuleTicketDetail mtd = saveDetail(jsonData);
			ticketListService.saveOrUpdateTicketDetail(mtd);
			return new AppResponse(HttpServletResponse.SC_OK, ""+mtd.getListId());

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}

	@RequestMapping(value = "forward", method = RequestMethod.POST)
	public @ResponseBody AppResponse forwardTicket(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestBody JsonNode jsonData) {

		try {
			String inputForwardOwnerType = jsonData.has("inputForwardOwnerType") ? jsonData.findValue("inputForwardOwnerType").asText() : null;
			String inputForwardOwner = jsonData.has("inputForwardOwner") ? jsonData.findValue("inputForwardOwner").asText() : null;
			ModuleTicketDetail mtd = saveDetail(jsonData);
			
			ticketListService.forwardTicket(mtd, inputForwardOwnerType, inputForwardOwner);

			return new AppResponse(HttpServletResponse.SC_OK, ""+mtd.getListId());

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(super.getLineNumber(), e.getMessage());

		} finally {
			initMenu(model, request);
		}
	}
	
	private ModuleTicketDetail saveDetail(JsonNode jsonData) {
		String inputContent = jsonData.has("inputContent") ? jsonData.findValue("inputContent").asText() : null;
		Long inputListId = jsonData.has("inputListId") ? jsonData.findValue("inputListId").asLong() : null;
		String inputOwnerStr = jsonData.has("inputOwnerStr") ? jsonData.findValue("inputOwnerStr").asText() : null;
		
		final String account = SecurityUtil.getSecurityUser().getUser().getUserName();
		final String username = SecurityUtil.getSecurityUser().getUsername()+"("+account+")";
		
		ModuleTicketDetail mtd = new ModuleTicketDetail();
		mtd.setListId(inputListId);
		mtd.setDetailOwner(inputOwnerStr);
		mtd.setContent(inputContent.replaceAll("\n", "<br>"));
		mtd.setCreateBy(username);
		mtd.setCreateTime(new Timestamp((new Date()).getTime()));
		
		return mtd;
	}
}
