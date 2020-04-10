package com.cmap.plugin.module.netflowpoller;

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
import com.cmap.service.DataPollerService;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;

@Controller
@RequestMapping("/plugin/module/netFlowTrace")
public class NetFlowTraceController extends BaseController {
	@Log
	private static Logger log;

	@Autowired
	private DataPollerService dataPollerService;

	@Autowired
	private NetFlowTraceService netFlowTraceService;

	@Autowired
	private DatabaseMessageSourceBase messageSource;

	//是否查詢條件為sensorId
	private boolean isSensorSearchMode = StringUtils.equalsIgnoreCase(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR, Constants.DATA_Y);
	
	/**
	 * 初始化選單
	 * @param model
	 * @param request
	 */
	private void initMenu(Model model, HttpServletRequest request) {
		Map<String, String> groupListMap = null;
		Map<String, String> sensorListMap = null;
		
		try {
			if(isSensorSearchMode) {
				if(StringUtils.isBlank(Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW)) {
					sensorListMap = getSensorList(request, null);
				}else {
					sensorListMap = getSensorList(request, Env.DEFAULT_DEVICE_ID_FOR_NET_FLOW);
				}
			}else {
				groupListMap = getGroupList(request);
			}			
			
		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("queryGroup", "");
			if(isSensorSearchMode) {
				model.addAttribute("groupList", sensorListMap);
			}else {
				model.addAttribute("groupList", groupListMap);
			}			
			
			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);

			model.addAttribute("pageLength", Env.NET_FLOW_PAGE_LENGTH);
		}
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String netFlow(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<String> tableTitleField = dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW_TRACE, DataPollerService.FIELD_TYPE_TARGET);
			model.addAttribute("TABLE_FIELD", tableTitleField);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_net_flow_trace";
	}

	@RequestMapping(value = "run", method = RequestMethod.GET)
	public String netFlowRun(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {

		try {
			dataPollerService.executePolling(Env.SETTING_ID_OF_NET_FLOW);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_net_flow_trace";
	}

	/**
	 * 取得查詢條件下總流量
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
	 * @return
	 */
	@RequestMapping(value = "getTotalTraffic.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse getTotalTraffic(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="querySourceIp", required=false, defaultValue="") String querySourceIp,
            @RequestParam(name="queryDestinationIp", required=false, defaultValue="") String queryDestinationIp,
            @RequestParam(name="querySenderIp", required=false, defaultValue="") String querySenderIp,
            @RequestParam(name="querySourcePort", required=false, defaultValue="") String querySourcePort,
            @RequestParam(name="queryDestinationPort", required=false, defaultValue="") String queryDestinationPort,
            @RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
            @RequestParam(name="searchValue", required=false, defaultValue="") String searchValue) {

	    String retVal = "N/A";
	    NetFlowTraceVO nfVO;
	    try {
	    	List<String> targetFieldList = new ArrayList<>();	        
	        targetFieldList.addAll(dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW_TRACE, DataPollerService.FIELD_TYPE_TARGET));
	        if(isSensorSearchMode) {
	        	targetFieldList.add(0, "Sensor_Id");
	        	targetFieldList.remove("SENSOR_ID");
	        }else {
	        	targetFieldList.add(0, "Group_Id");
	        }   
	        
            nfVO = new NetFlowTraceVO();
            if(isSensorSearchMode) {
            	nfVO.setQuerySensorId(queryGroup);
            }else {
            	nfVO.setQueryGroupId(queryGroup);
            }            
            nfVO.setQuerySourceIp(querySourceIp);
            nfVO.setQuerySourcePort(querySourcePort);
            nfVO.setQueryDestinationIp(queryDestinationIp);
            nfVO.setQueryDestinationPort(queryDestinationPort);
            nfVO.setQuerySenderIp(querySenderIp);
            nfVO.setQueryDateBegin(queryDateBegin);
            nfVO.setQueryTimeBegin(queryTimeBegin);
            nfVO.setQueryTimeEnd(queryTimeEnd);
            nfVO.setQueryDateStr(queryDateBegin.replace("-", ""));
            nfVO.setQueryTimeBeginStr(queryTimeBegin.replace(":", ""));
            nfVO.setQueryTimeEndStr(queryTimeEnd.replace(":", ""));
            nfVO.setSearchValue(searchValue);

            String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_NET_FLOW);

            if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
                /*
                 * Option 1. 走 FILE 模式查詢
                 */

            } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
                /*
                 * Option 2. 走 DB 模式查詢
                 */
                retVal = netFlowTraceService.getTotalTraffic(nfVO, targetFieldList);
            }

            AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "SUCCESS");
            app.putData(Constants.APP_DATA_KEY_TTL_TRAFFIC, retVal);
            return app;

	    } catch (Exception e) {
	        log.error(e.toString(), e);

	        AppResponse app = new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, "ERROR");
            app.putData(Constants.APP_DATA_KEY_TTL_TRAFFIC, retVal);
            return app;
	    }
	}

	/**
	 * 取得查詢條件下總筆數
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
	 * @return
	 */
	@RequestMapping(value = "getTotalFilteredCount.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse getTotalFilteredCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="querySourceIp", required=false, defaultValue="") String querySourceIp,
            @RequestParam(name="queryDestinationIp", required=false, defaultValue="") String queryDestinationIp,
            @RequestParam(name="querySenderIp", required=false, defaultValue="") String querySenderIp,
            @RequestParam(name="querySourcePort", required=false, defaultValue="") String querySourcePort,
            @RequestParam(name="queryDestinationPort", required=false, defaultValue="") String queryDestinationPort,
            @RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
            @RequestParam(name="searchValue", required=false, defaultValue="") String searchValue) {

	    String retVal = "N/A";
	    long filteredTotal = 0;
        NetFlowTraceVO nfVO;
        try {
        	List<String> targetFieldList = new ArrayList<>();	        
	        targetFieldList.addAll(dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW_TRACE, DataPollerService.FIELD_TYPE_TARGET));
	        if(isSensorSearchMode) {
	        	targetFieldList.add(0, "Sensor_Id");
	        	targetFieldList.remove("SENSOR_ID");
	        }else {
	        	targetFieldList.add(0, "Group_Id");
	        }   
	        
            nfVO = new NetFlowTraceVO();
            if(isSensorSearchMode) {
            	nfVO.setQuerySensorId(queryGroup);
            }else {
            	nfVO.setQueryGroupId(queryGroup);
            }   
            nfVO.setQuerySourceIp(querySourceIp);
            nfVO.setQuerySourcePort(querySourcePort);
            nfVO.setQueryDestinationIp(queryDestinationIp);
            nfVO.setQueryDestinationPort(queryDestinationPort);
            nfVO.setQuerySenderIp(querySenderIp);
            nfVO.setQueryDateBegin(queryDateBegin);
            nfVO.setQueryTimeBegin(queryTimeBegin);
            nfVO.setQueryTimeEnd(queryTimeEnd);
            nfVO.setQueryDateStr(queryDateBegin.replace("-", ""));
            nfVO.setQueryTimeBeginStr(queryTimeBegin.replace(":", ""));
            nfVO.setQueryTimeEndStr(queryTimeEnd.replace(":", ""));
            nfVO.setSearchValue(searchValue);

            String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_NET_FLOW);

            if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
                /*
                 * Option 1. 走 FILE 模式查詢
                 */

            } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
                /*
                 * Option 2. 走 DB 模式查詢
                 */
                filteredTotal = netFlowTraceService.countNetFlowRecordFromDB(nfVO, targetFieldList);
                retVal = Constants.NUMBER_FORMAT_THOUSAND_SIGN.format(filteredTotal);
            }

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

	@RequestMapping(value = "getNetFlowData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getNetFlowData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
			@RequestParam(name="querySourceIp", required=false, defaultValue="") String querySourceIp,
			@RequestParam(name="queryDestinationIp", required=false, defaultValue="") String queryDestinationIp,
			@RequestParam(name="querySenderIp", required=false, defaultValue="") String querySenderIp,
			@RequestParam(name="querySourcePort", required=false, defaultValue="") String querySourcePort,
			@RequestParam(name="queryDestinationPort", required=false, defaultValue="") String queryDestinationPort,
			@RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
			@RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
			@RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filteredTotal = 0;
		String totalFlow = "";
		List<NetFlowTraceVO> dataList = new ArrayList<>();
	    try {	
	    	/*
			if (StringUtils.isBlank(queryGroup)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("group.name", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowTraceVO>(), new Long(0), msg);
	        }*/
	        if (StringUtils.isBlank(queryDateBegin)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowTraceVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowTraceVO>(), new Long(0), msg);
	        }
	        if (StringUtils.isBlank(querySourceIp) && StringUtils.isBlank(queryDestinationIp)&&StringUtils.isBlank(queryGroup)) {
	            String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("net.flow.source.ip", Locale.TAIWAN, null)
	            + "or" + messageSource.getMessage("net.flow.destination.ip", Locale.TAIWAN, null);
	            return new DatatableResponse(new Long(0), new ArrayList<NetFlowTraceVO>(), new Long(0), msg);
	        }

	        NetFlowTraceVO resultVO = doDataQuery(queryGroup, querySourceIp, queryDestinationIp, querySenderIp,
            	                    querySourcePort, queryDestinationPort, queryMac, queryDateBegin, queryDateEnd,
            	                    queryTimeBegin, queryTimeEnd, startNum, pageLength, searchValue,
            	                    orderColIdx, orderDirection
            	                 );
	        filteredTotal = resultVO.getMatchedList().size();
            dataList = resultVO.getMatchedList();
            total = resultVO.getTotalCount();

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filteredTotal, null, totalFlow);
	}

	private NetFlowTraceVO doDataQuery(String queryGroup, String querySourceIp, String queryDestinationIp, String querySenderIp,
	    String querySourcePort, String queryDestinationPort, String queryMac, String queryDateBegin, String queryDateEnd,
        String queryTimeBegin, String queryTimeEnd, Integer startNum, Integer pageLength, String searchValue,
        Integer orderColIdx, String orderDirection) throws ServiceLayerException {

	    NetFlowTraceVO retVO = new NetFlowTraceVO();
	    try {
	    	List<String> targetFieldList = new ArrayList<>();	        
	        targetFieldList.addAll(dataPollerService.getFieldName(Env.SETTING_ID_OF_NET_FLOW_TRACE, DataPollerService.FIELD_TYPE_TARGET));
	        if(isSensorSearchMode) {
	        	targetFieldList.add(0, "Sensor_Id");
	        	targetFieldList.remove("SENSOR_ID");
	        }else {
	        	targetFieldList.add(0, "Group_Id");
	        }   
	        
	        NetFlowTraceVO nfVO = new NetFlowTraceVO();
            if(isSensorSearchMode) {
            	nfVO.setQuerySensorId(queryGroup);
            }else {
            	nfVO.setQueryGroupId(queryGroup);
            }
	        nfVO.setQuerySourceIp(querySourceIp);
	        nfVO.setQuerySourcePort(querySourcePort);
	        nfVO.setQueryDestinationIp(queryDestinationIp);
	        nfVO.setQueryDestinationPort(queryDestinationPort);
	        nfVO.setQuerySenderIp(querySenderIp);
	        nfVO.setQueryDateBegin(queryDateBegin);
	        nfVO.setQueryTimeBegin(queryTimeBegin);
	        nfVO.setQueryTimeEnd(queryTimeEnd);
	        nfVO.setQueryDateStr(queryDateBegin.replace("-", ""));
	        nfVO.setQueryTimeBeginStr(queryTimeBegin.replace(":", ""));
	        nfVO.setQueryTimeEndStr(queryTimeEnd.replace(":", ""));
	        nfVO.setStartNum(startNum);
	        nfVO.setPageLength(pageLength);
	        nfVO.setSearchValue(searchValue);
	        //nfVO.setOrderColumn(getOrderColumnName(orderColIdx)); //效能issue，暫定限制僅能用From_Date_Time排序
	        nfVO.setOrderColumn("From_Time");
	        nfVO.setOrderDirection(orderDirection);

	        String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_NET_FLOW);

	        if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
	            /*
	             * Option 1. 走 FILE 模式查詢
	             */
	            retVO = netFlowTraceService.findNetFlowRecordFromFile(nfVO, startNum, pageLength);

	        } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
	            /*
	             * Option 2. 走 DB 模式查詢
	             */
	            List<NetFlowTraceVO> dataList = new ArrayList<>();
	            dataList = netFlowTraceService.findNetFlowRecordFromDB(nfVO, startNum, pageLength, targetFieldList);
	            retVO.setMatchedList(dataList);
	        }
	    } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
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
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="querySourceIp", required=false, defaultValue="") String querySourceIp,
            @RequestParam(name="queryDestinationIp", required=false, defaultValue="") String queryDestinationIp,
            @RequestParam(name="querySenderIp", required=false, defaultValue="") String querySenderIp,
            @RequestParam(name="querySourcePort", required=false, defaultValue="") String querySourcePort,
            @RequestParam(name="queryDestinationPort", required=false, defaultValue="") String queryDestinationPort,
            @RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd,
            @RequestParam(name="searchValue", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

	    try {
	        Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

	        NetFlowTraceVO resultVO = doDataQuery(queryGroup, querySourceIp, queryDestinationIp, querySenderIp,
                                    querySourcePort, queryDestinationPort, queryMac, queryDateBegin, queryDateEnd,
                                    queryTimeBegin, queryTimeEnd, queryStartNum, queryPageLength, searchValue,
                                    orderColIdx, orderDirection
                                 );

	        List<NetFlowTraceVO> dataList = resultVO.getMatchedList();

	        if (dataList != null && !dataList.isEmpty()) {
	            String fileName = getFileName(Env.EXPORT_DATA_CSV_FILE_NAME_OF_NET_FLOW);
	            String[] fieldNames = new String[] {
	                    "groupName", "now", "fromDateTime", "toDateTime", "ethernetType", "protocol",
	                    "sourceIP", "sourcePort", "sourceMAC", "destinationIP", "destinationPort",
	                    "destinationMAC", "size","session", "channelID", "toS", "senderIP", "inboundInterface",
	                    "outboundInterface", "sourceASI", "destinationASI", "sourceMask", "destinationMask",
	                    "nextHop", "sourceVLAN", "destinationVLAN", "flowID"
	            };
	            String[] columnsTitles = Env.EXPORT_DATA_CSV_COLUMNS_TITLES_OF_NET_FLOW_TRACE.split(",");

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
