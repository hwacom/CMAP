package com.cmap.plugin.module.firewall;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.cmap.plugin.module.netflow.NetFlowVO;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DataPollerService;
import com.google.gson.Gson;

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
        Map<String, String> actionListMap = null;
        try {
            typeListMap = getMenuItem("FIREWALL_LOG_QUERY_TYPE", true);
            devNameListMap = getMenuItem("FIREWALL_LOG_QUERY_DEV_NAME", true);
            actionListMap = new HashMap<>();
            actionListMap.put("=== ALL ===", "");

        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            model.addAttribute("queryType", "");
            model.addAttribute("queryTypeMobile", "");
            model.addAttribute("typeList", typeListMap);

            model.addAttribute("queryDevName", "");
            model.addAttribute("queryDevNameMobile", "");
            model.addAttribute("devNameList", devNameListMap);

            model.addAttribute("queryAction", "");
            model.addAttribute("queryActionMobile", "");
            model.addAttribute("actionList", actionListMap);

            model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
            model.addAttribute("timeout", Env.TIMEOUT_4_FIREWALL_LOG_QUERY);

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

        return "plugin/module_firewall";
    }

    private String getOrderColumnName(String queryType, Integer orderColIdx) {
        String retVal = "TYPE";
        try {
            List<String> typeFieldList = firewallService.getFieldNameList(queryType, DataPollerService.FIELD_TYPE_TARGET);
            /*
             * 查詢結果UI欄位為所有類別的欄位總和，因此這邊在抓order by欄位時需改成從所有欄位抓取
             */
            List<FirewallVO> voList = firewallService.findFirewallLogSetting("UI_FIELDS");

            if (voList != null && !voList.isEmpty()) {
                if (typeFieldList == null || (typeFieldList != null && typeFieldList.isEmpty())) {

                } else {
                    if (orderColIdx < voList.size()) {
                        String settingVal = voList.get(orderColIdx).getSettingValue();

                        boolean hasField = false;
                        for (String fName : typeFieldList) {
                            if (StringUtils.equals(settingVal, fName)) {
                                retVal = fName;
                                hasField = true;
                                break;
                            }
                        }

                        if (!hasField) {
                            retVal = "DATE";
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return retVal;
    }

    private List<String> getFieldNameList(String queryType) {
        return firewallService.getFieldNameList(queryType, DataPollerService.FIELD_TYPE_TARGET);
    }

    @RequestMapping(value = "getActionMenu", method = RequestMethod.POST, produces="application/json;odata=verbose")
    public @ResponseBody AppResponse getActionMenu(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryType", required=false) String queryType) {

        Map<String, String> actionMap;
        try {
            AppResponse appResponse;
            if (StringUtils.isBlank(queryType)) {
                appResponse = new AppResponse(HttpServletResponse.SC_NO_CONTENT, "類別未選擇，動作選單清空");
                return appResponse;
            }

            actionMap = firewallService.getActionMenu(queryType);

            if (actionMap != null && !actionMap.isEmpty()) {
                appResponse = new AppResponse(HttpServletResponse.SC_OK, "取得動作選單成功");
                appResponse.putData("action",  new Gson().toJson(actionMap));

            } else {
                appResponse = new AppResponse(HttpServletResponse.SC_NOT_FOUND, "無法取得動作選單");
            }

            return appResponse;

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping(value = "getFirewallLogCount.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse getFirewallLogCount(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryType", required=false, defaultValue=Constants.FIREWALL_LOG_TYPE_ALL) String queryType,
            @RequestParam(name="queryDevName", required=true, defaultValue="") String queryDevName,
            @RequestParam(name="querySrcIp", required=false, defaultValue="") String querySrcIp,
            @RequestParam(name="querySrcPort", required=false, defaultValue="") String querySrcPort,
            @RequestParam(name="queryDstIp", required=false, defaultValue="") String queryDstIp,
            @RequestParam(name="queryDstPort", required=false, defaultValue="") String queryDstPort,
            @RequestParam(name="queryAction", required=false, defaultValue="") String queryAction,
            @RequestParam(name="queryDateBegin", required=true, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=true, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=true, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=true, defaultValue="") String queryTimeEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        String retVal = "N/A";
        long filteredTotal = 0;
        FirewallVO fVO;
        try {
            String typeNameApp = messageSource.getMessage("firewall.log.type.app", Locale.TAIWAN, null);
            String typeNameForwarding = messageSource.getMessage("firewall.log.type.forwarding", Locale.TAIWAN, null);
            String typeNameSystem = messageSource.getMessage("firewall.log.type.system", Locale.TAIWAN, null);
            String typeNameIntrusion = messageSource.getMessage("firewall.log.type.intrusion", Locale.TAIWAN, null);
            String typeNameWebFilter = messageSource.getMessage("firewall.log.type.web", Locale.TAIWAN, null);

            Map<String, String> typeNameMap = new HashMap<>();
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_APP, typeNameApp);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_FORWARDING, typeNameForwarding);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_INTRUSION, typeNameSystem);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_SYSTEM, typeNameIntrusion);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_WEBFILTER, typeNameWebFilter);

            /*
             * 取得各TABLE的查詢欄位LIST for 後續查詢SQL的「select」及「where like」部分使用
             */
            Map<String, List<String>> fieldsMap = new HashMap<>();
            List<String> targetFieldList = null;
            List<String> allTitleField = null;
            List<String> appTitleField = null;
            List<String> forwardingTitleField = null;
            List<String> intrusionTitleField = null;
            List<String> systemTitleField = null;
            List<String> webfilterTitleField = null;

            if (!StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_ALL)) {
                targetFieldList = getFieldNameList(queryType);

                fieldsMap.put(queryType, targetFieldList);

            } else {
                allTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_ALL);
                appTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_APP);
                forwardingTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_FORWARDING);
                intrusionTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_INTRUSION);
                systemTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_SYSTEM);
                webfilterTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_WEBFILTER);

                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_ALL, allTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_APP, appTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_FORWARDING, forwardingTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_INTRUSION, intrusionTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_SYSTEM, systemTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_WEBFILTER, webfilterTitleField);
            }

            fVO = new FirewallVO();
            fVO.setQueryType(queryType);
            fVO.setQueryDevName(queryDevName);

            // 查詢類別非「System」時才有IP、Port、動作查詢條件
            if (!StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_SYSTEM)) {
                fVO.setQuerySrcIp(querySrcIp);
                fVO.setQuerySrcPort(querySrcPort);
                fVO.setQueryDstIp(queryDstIp);
                fVO.setQueryDstPort(queryDstPort);
                fVO.setQueryAction(queryAction);
            }

            fVO.setQueryDateBegin(queryDateBegin);
            fVO.setQueryDateEnd(queryDateEnd);
            fVO.setQueryTimeBegin(queryTimeBegin);
            fVO.setQueryTimeEnd(queryTimeEnd);
            fVO.setStartNum(startNum);
            fVO.setPageLength(pageLength);
            fVO.setSearchValue(searchValue);
            fVO.setOrderColumn(getOrderColumnName(queryType, orderColIdx));
            fVO.setOrderDirection(orderDirection);
            fVO.setTypeNameMap(typeNameMap);

            int beginMonth = queryDateBegin.indexOf("-") != -1
                                ? Integer.valueOf(queryDateBegin.split("-")[1]) : 0;
            int endMonth = queryDateEnd.indexOf("-") != -1
                                ? Integer.valueOf(queryDateEnd.split("-")[1]) : 0;

            fVO.setQueryMonths(new int[] {beginMonth, endMonth});

            String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_FIREWALL_LOG);

            if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
                /*
                 * Option 1. 走 FILE 模式查詢
                 */

            } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
                /*
                 * Option 2. 走 DB 模式查詢
                 */
                filteredTotal =
                        StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_ALL)
                            ? firewallService.countFirewallLogRecordFromDBbyAll(fVO, fieldsMap)
                            : firewallService.countFirewallLogRecordFromDB(fVO, fieldsMap);
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

    @RequestMapping(value = "getFirewallLogData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getFirewallLogData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryType", required=false, defaultValue=Constants.FIREWALL_LOG_TYPE_ALL) String queryType,
            @RequestParam(name="queryDevName", required=true, defaultValue="") String queryDevName,
            @RequestParam(name="querySrcIp", required=false, defaultValue="") String querySrcIp,
            @RequestParam(name="querySrcPort", required=false, defaultValue="") String querySrcPort,
            @RequestParam(name="queryDstIp", required=false, defaultValue="") String queryDstIp,
            @RequestParam(name="queryDstPort", required=false, defaultValue="") String queryDstPort,
            @RequestParam(name="queryAction", required=false, defaultValue="") String queryAction,
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
        long filteredTotal = 0;
        String totalFlow = "";
        List<FirewallVO> dataList = new ArrayList<>();
        FirewallVO fVO;
        try {
            /* 類別改為可以選ALL，不檢核
            if (StringUtils.isBlank(queryType)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("firewall.type", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }
            */
            if (StringUtils.isBlank(queryDateBegin)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("date", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }
            if (StringUtils.isBlank(queryTimeBegin) || StringUtils.isBlank(queryTimeEnd)) {
                String msg = messageSource.getMessage("please.choose", Locale.TAIWAN, null) + messageSource.getMessage("time", Locale.TAIWAN, null);
                return new DatatableResponse(new Long(0), new ArrayList<NetFlowVO>(), new Long(0), msg);
            }

            String typeNameApp = messageSource.getMessage("firewall.log.type.app", Locale.TAIWAN, null);
            String typeNameForwarding = messageSource.getMessage("firewall.log.type.forwarding", Locale.TAIWAN, null);
            String typeNameSystem = messageSource.getMessage("firewall.log.type.system", Locale.TAIWAN, null);
            String typeNameIntrusion = messageSource.getMessage("firewall.log.type.intrusion", Locale.TAIWAN, null);
            String typeNameWebFilter = messageSource.getMessage("firewall.log.type.web", Locale.TAIWAN, null);

            Map<String, String> typeNameMap = new HashMap<>();
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_APP, typeNameApp);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_FORWARDING, typeNameForwarding);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_INTRUSION, typeNameSystem);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_SYSTEM, typeNameIntrusion);
            typeNameMap.put(Constants.FIREWALL_LOG_TYPE_WEBFILTER, typeNameWebFilter);

            /*
             * 取得各TABLE的查詢欄位LIST for 後續查詢SQL的「select」及「where like」部分使用
             */
            Map<String, List<String>> fieldsMap = new HashMap<>();
            List<String> targetFieldList = null;
            List<String> allTitleField = null;
            List<String> appTitleField = null;
            List<String> forwardingTitleField = null;
            List<String> intrusionTitleField = null;
            List<String> systemTitleField = null;
            List<String> webfilterTitleField = null;

            if (!StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_ALL)) {
                targetFieldList = getFieldNameList(queryType);

                fieldsMap.put(queryType, targetFieldList);

            } else {
                allTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_ALL);
                appTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_APP);
                forwardingTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_FORWARDING);
                intrusionTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_INTRUSION);
                systemTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_SYSTEM);
                webfilterTitleField = getFieldNameList(Constants.FIREWALL_LOG_TYPE_WEBFILTER);

                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_ALL, allTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_APP, appTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_FORWARDING, forwardingTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_INTRUSION, intrusionTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_SYSTEM, systemTitleField);
                fieldsMap.put(Constants.FIREWALL_LOG_TYPE_WEBFILTER, webfilterTitleField);
            }

            fVO = new FirewallVO();
            fVO.setQueryType(queryType);
            fVO.setQueryDevName(queryDevName);

            // 查詢類別非「System」時才有IP、Port、動作查詢條件
            if (!StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_SYSTEM)) {
                fVO.setQuerySrcIp(querySrcIp);
                fVO.setQuerySrcPort(querySrcPort);
                fVO.setQueryDstIp(queryDstIp);
                fVO.setQueryDstPort(queryDstPort);
                fVO.setQueryAction(queryAction);
            }

            fVO.setQueryDateBegin(queryDateBegin);
            fVO.setQueryDateEnd(queryDateEnd);
            fVO.setQueryTimeBegin(queryTimeBegin);
            fVO.setQueryTimeEnd(queryTimeEnd);
            fVO.setStartNum(startNum);
            fVO.setPageLength(pageLength);
            fVO.setSearchValue(searchValue);
            fVO.setOrderColumn(getOrderColumnName(queryType, orderColIdx));
            fVO.setOrderDirection(orderDirection);
            fVO.setTypeNameMap(typeNameMap);

            int beginMonth = queryDateBegin.indexOf("-") != -1
                                ? Integer.valueOf(queryDateBegin.split("-")[1]) : 0;
            int endMonth = queryDateEnd.indexOf("-") != -1
                                ? Integer.valueOf(queryDateEnd.split("-")[1]) : 0;

            fVO.setQueryMonths(new int[] {beginMonth, endMonth});

            String storeMethod = dataPollerService.getStoreMethodByDataType(Constants.DATA_TYPE_OF_FIREWALL_LOG);

            if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_FILE)) {
                /*
                 * Option 1. 走 FILE 模式查詢
                 */

            } else if (StringUtils.equals(storeMethod, Constants.STORE_METHOD_OF_DB)) {
                /*
                 * Option 2. 走 DB 模式查詢
                 */
                dataList =
                        StringUtils.equals(queryType, Constants.FIREWALL_LOG_TYPE_ALL)
                        ? firewallService.findFirewallLogRecordFromDBbyAll(fVO, startNum, pageLength, fieldsMap)
                        : firewallService.findFirewallLogRecordFromDB(fVO, startNum, pageLength, fieldsMap);

                filteredTotal = dataList.size();
                total = firewallService.getTableRoughlyTotalCount(fVO);
            }

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(total, dataList, filteredTotal, null, totalFlow);
    }
}
