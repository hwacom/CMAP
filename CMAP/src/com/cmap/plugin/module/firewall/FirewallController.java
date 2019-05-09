package com.cmap.plugin.module.firewall;

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
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DataPollerService;

@Controller
@RequestMapping("/plugin/module/firewall/log")
public class FirewallController extends BaseController {
    @Log
    private static Logger log;

    @Autowired
    private FirewallService firewallService;

    @Autowired
    private DataPollerService dataPollerService;

    @Autowired
    private DatabaseMessageSourceBase messageSource;

    /**
     * 初始化選單
     * @param model
     * @param request
     */
    private void initMenu(Model model, HttpServletRequest request) {
        Map<String, String> typeListMap = null;
        Map<String, String> devNameListMap = null;
        try {
            typeListMap = getMenuItem("FIREWALL_LOG_QUERY_TYPE", true);
            devNameListMap = getMenuItem("FIREWALL_LOG_QUERY_DEV_NAME", true);

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            model.addAttribute("queryType", "");
            model.addAttribute("queryTypeMobile", "");
            model.addAttribute("typeList", typeListMap);

            model.addAttribute("queryDevName", "");
            model.addAttribute("queryDevNameMobile", "");
            model.addAttribute("devNameList", devNameListMap);

            model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
            model.addAttribute("timeout", 3600);
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

        return "plugin/module_firewall";
    }

    private String getOrderColumnName(String queryType, Integer orderColIdx) {
        String retVal = "Date";
        try {
            /*
             * TODO
             * 查詢結果UI欄位為所有類別的欄位總和，因此這邊在抓order by欄位時需改成從所有欄位抓取
             */
            List<String> tableTitleField = getFieldNameList(queryType);

            if (tableTitleField != null && !tableTitleField.isEmpty()) {
                retVal = (orderColIdx < tableTitleField.size()) ? tableTitleField.get(orderColIdx) : retVal;
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return retVal;
    }

    private List<String> getFieldNameList(String queryType) {
        return firewallService.getFieldNameList(queryType, DataPollerService.FIELD_TYPE_TARGET);
    }

    @RequestMapping(value = "getFirewallLogData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getFirewallLogData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryType", required=true, defaultValue="") String queryType,
            @RequestParam(name="queryDevName", required=true, defaultValue="") String queryDevName,
            @RequestParam(name="querySrcIp", required=false, defaultValue="") String querySrcIp,
            @RequestParam(name="querySrcPort", required=false, defaultValue="") String querySrcPort,
            @RequestParam(name="queryDstIp", required=false, defaultValue="") String queryDstIp,
            @RequestParam(name="queryDstPort", required=false, defaultValue="") String queryDstPort,
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=true, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=true, defaultValue="") String queryTimeEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        String totalFlow = "";
        List<FirewallVO> dataList = new ArrayList<>();
        FirewallVO fVO;
        try {
            if (StringUtils.isBlank(queryType)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("firewall.type", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }
            if (StringUtils.isBlank(queryDateBegin)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }
            if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }

            List<String> targetFieldList = getFieldNameList(queryType);

            fVO = new FirewallVO();
            fVO.setQueryType(queryType);
            fVO.setQueryDevName(queryDevName);
            fVO.setQuerySrcIp(querySrcIp);
            fVO.setQuerySrcPort(querySrcPort);
            fVO.setQueryDstIp(queryDstIp);
            fVO.setQueryDstPort(queryDstPort);
            fVO.setQueryDateBegin(queryDateBegin);
            fVO.setQueryDateEnd(queryDateEnd);
            fVO.setQueryTimeBegin(queryTimeBegin);
            fVO.setQueryTimeEnd(queryTimeEnd);
            fVO.setStartNum(startNum);
            fVO.setPageLength(pageLength);
            fVO.setSearchValue(searchValue);
            fVO.setOrderColumn(getOrderColumnName(queryType, orderColIdx));
            fVO.setOrderDirection(orderDirection);

            String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_FIREWALL_LOG);

            if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
                /*
                 * Option 1. 走 FILE 模式查詢
                 */

            } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
                /*
                 * Option 2. 走 DB 模式查詢
                 */
                filterdTotal = firewallService.countFirewallLogRecordFromDB(fVO, targetFieldList);

                if (filterdTotal != 0) {
                    dataList = firewallService.findFirewallLogRecordFromDB(fVO, startNum, pageLength, targetFieldList);
                }

                FirewallVO newVO = new FirewallVO();
                newVO.setQueryType(queryType);
                newVO.setQueryDevName(queryDevName);
                total = firewallService.countFirewallLogRecordFromDB(newVO, targetFieldList);
            }

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(total, dataList, filterdTotal, null, totalFlow);
    }
}
