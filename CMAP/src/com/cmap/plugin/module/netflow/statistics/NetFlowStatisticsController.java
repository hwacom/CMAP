package com.cmap.plugin.module.netflow.statistics;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.cmap.security.SecurityUtil;
import com.cmap.utils.DataExportUtils;
import com.cmap.utils.impl.CsvExportUtils;

@Controller
@RequestMapping("/plugin/module/netFlow/ranking")
public class NetFlowStatisticsController extends BaseController {
    @Log
    private static Logger log;

    @Autowired
    private NetFlowStatisticsService netFlowStatisticsService;

    private static final String[] UI_TABLE_COLUMNS = new String[] {"","mits1.ip_Address","mids.ip_desc","mits1.group_Id","percent","ttl_traffic","ttl_upload_traffic","ttl_download_traffic"};
    //是否查詢條件為sensorId
  	private boolean isSensorSearchMode = StringUtils.isNotBlank(Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR) && Env.NET_FLOW_SEARCH_MODE_WITH_SENSOR.equalsIgnoreCase(Constants.DATA_Y);
  	
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
        }
    }

    @RequestMapping(value = "traffic", method = RequestMethod.GET)
    public String trafficMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_net_flow_ranking_traffic";
    }

    @RequestMapping(value = "traffic/all", method = RequestMethod.GET)
    public String trafficAllMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_net_flow_ranking_traffic";
    }

    @RequestMapping(value = "session", method = RequestMethod.GET)
    public String sessionMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_net_flow_ranking_session";
    }

    @RequestMapping(value = "session/all", method = RequestMethod.GET)
    public String sessionAllMain(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_net_flow_ranking_session";
    }

    @RequestMapping(value = "getNetFlowRankingTrafficData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getNetFlowTrafficData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
            @RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        String totalFlow = "";
        List<NetFlowStatisticsVO> dataList = null;
        NetFlowStatisticsVO nfsVO;
        try {
            nfsVO = new NetFlowStatisticsVO();
            nfsVO.setQueryGroupId(queryGroup);

            Calendar nowDate = Calendar.getInstance();
            nowDate.setTime(new Date());
            nowDate.set(Calendar.HOUR_OF_DAY, 0);
            nowDate.set(Calendar.MINUTE, 0);
            nowDate.set(Calendar.SECOND, 0);
            nowDate.set(Calendar.MILLISECOND, 0);
            nfsVO.setQueryDateBegin(queryDateBegin);
            nfsVO.setQueryDateEnd(queryDateEnd);
            nfsVO.setStartNum(startNum);
            nfsVO.setPageLength(pageLength);
            nfsVO.setSearchValue(searchValue);
            nfsVO.setOrderColumn(UI_TABLE_COLUMNS[orderColIdx]);
            nfsVO.setOrderDirection(orderDirection);

            filterdTotal = netFlowStatisticsService.countModuleIpTrafficStatistics(nfsVO);

            if (filterdTotal > 0) {
                dataList = netFlowStatisticsService.findModuleIpTrafficStatistics(nfsVO);
            } else {
                dataList = new ArrayList<>();
            }

            if (StringUtils.isBlank(searchValue)) {
                total = filterdTotal;
            } else {
                nfsVO.setSearchValue(null);
                total = netFlowStatisticsService.countModuleIpTrafficStatistics(nfsVO);
            }

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(total, dataList, filterdTotal, null, totalFlow);
    }

    @RequestMapping(value = "trafficDataExport.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse trafficDataExport(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
            @RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection,
            @RequestParam(name="var1", required=true, defaultValue="") String var1,
            @RequestParam(name="exportRecordCount", required=true, defaultValue="") String exportRecordCount) {

        List<NetFlowStatisticsVO> dataList = null;
        NetFlowStatisticsVO nfsVO;
        try {
            Integer queryStartNum = 0;
            Integer queryPageLength = getDataExportRecordCount(exportRecordCount);

            nfsVO = new NetFlowStatisticsVO();
            nfsVO.setQueryGroupId(queryGroup);
            nfsVO.setQueryDateBegin(queryDateBegin);
            nfsVO.setQueryDateEnd(queryDateEnd);
            nfsVO.setStartNum(queryStartNum);
            nfsVO.setPageLength(queryPageLength);
            nfsVO.setSearchValue(searchValue);
            nfsVO.setOrderColumn(UI_TABLE_COLUMNS[orderColIdx]);
            nfsVO.setOrderDirection(orderDirection);
            dataList = netFlowStatisticsService.findModuleIpTrafficStatistics(nfsVO);

            if (dataList != null && !dataList.isEmpty()) {
                String fileName = getFileName(Env.EXPORT_DATA_CSV_FILE_NAME_OF_TRAFFIC_RANK, var1);
                String[] fieldNames = new String[] {
                        "ipAddress", "ipDesc", "groupName", "percent", "totalTraffic", "uploadTraffic", "downloadTraffic"
                };
                String[] columnsTitles = Env.EXPORT_DATA_CSV_COLUMNS_TITLES_OF_TRAFFIC_RANK.split(",");

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

    @RequestMapping(value = "getNetFlowRankingSessionData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getNetFlowSessionData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
            @RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        String totalFlow = "";
        List<NetFlowStatisticsVO> dataList = null;
        NetFlowStatisticsVO nfsVO;
        try {
            nfsVO = new NetFlowStatisticsVO();
            nfsVO.setQueryGroupId(queryGroup);
            nfsVO.setQueryDateBegin(queryDateBegin);
            nfsVO.setQueryDateEnd(queryDateEnd);
            nfsVO.setStartNum(startNum);
            nfsVO.setPageLength(pageLength);
            nfsVO.setSearchValue(searchValue);
            nfsVO.setOrderColumn(UI_TABLE_COLUMNS[orderColIdx]);
            nfsVO.setOrderDirection(orderDirection);

            filterdTotal = netFlowStatisticsService.countModuleIpTrafficStatistics(nfsVO);

            if (filterdTotal > 0) {
                dataList = netFlowStatisticsService.findModuleIpTrafficStatistics(nfsVO);
            } else {
                dataList = new ArrayList<>();
            }

            if (StringUtils.isBlank(searchValue)) {
                total = filterdTotal;
            } else {
                nfsVO.setSearchValue(null);
                total = netFlowStatisticsService.countModuleIpTrafficStatistics(nfsVO);
            }

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(total, dataList, filterdTotal, null, totalFlow);
    }
}
