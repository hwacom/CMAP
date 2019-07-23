package com.cmap.plugin.module.netflow.statistics;

import java.security.Principal;
import java.util.ArrayList;
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
import com.cmap.DatatableResponse;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.security.SecurityUtil;

@Controller
@RequestMapping("/plugin/module/netFlow/ranking")
public class NetFlowStatisticsController extends BaseController {
    @Log
    private static Logger log;

    @Autowired
    private NetFlowStatisticsService netFlowStatisticsService;

    private static final String[] UI_TABLE_COLUMNS = new String[] {"","","dl.group_Name","dl.device_Name","dl.system_Version","cvi.config_Version","cvi.config_Type","cvi.create_Time"};

    /**
     * 初始化選單
     * @param model
     * @param request
     */
    private void initMenu(Model model, HttpServletRequest request) {
        Map<String, String> groupListMap = null;
        try {
            groupListMap = getGroupList(request);

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            model.addAttribute("queryGroup", "");
            model.addAttribute("groupList", groupListMap);

            model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
            model.addAttribute("timeout", Env.TIMEOUT_4_NET_FLOW_QUERY);
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
        return "plugin/module_net_flow_ranking";
    }

    @RequestMapping(value = "all", method = RequestMethod.GET)
    public String mainAll(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_net_flow_ranking";
    }

    @RequestMapping(value = "getNetFlowRankingData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getNetFlowData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
            @RequestParam(name="queryDatePeriod", required=true, defaultValue="") String queryDatePeriod,
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
