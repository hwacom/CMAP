package com.cmap.plugin.module.ip.mapping;

import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
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
import com.cmap.security.SecurityUtil;

@Controller
@RequestMapping("/plugin/module/ipMapping")
public class IpMappingController extends BaseController {
    @Log
    private static Logger log;

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

    /**
     * IP/MAC/Port mapping紀錄頁面
     * @param model
     * @param principal
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "record", method = RequestMethod.GET)
    public String record(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {


        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
        }
        return "plugin/module_ip_mapping_record";
    }

    /**
     * IP/MAC/Port mapping異動紀錄頁面
     * @param model
     * @param principal
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "change", method = RequestMethod.GET)
    public String change(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {


        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
            initMenu(model, request);
        }
        return "plugin/module_ip_mapping_change";
    }

    /**
     * 查找 IP/MAC/Port mapping異動紀錄
     * @param model
     * @param request
     * @param response
     * @param queryGroup
     * @param queryIp
     * @param queryMac
     * @param queryPort
     * @param queryDateBegin
     * @param queryDateEnd
     * @param startNum
     * @param pageLength
     * @param searchValue
     * @param orderColIdx
     * @param orderDirection
     * @return
     */
    @RequestMapping(value = "getChangeRecord.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getChangeRecord(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="queryIp", required=false, defaultValue="") String queryIp,
            @RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
            @RequestParam(name="queryPort", required=false, defaultValue="") String queryPort,
            @RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        try {


        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(0L, null, 0L);
    }

    /**
     * 查找某一時段內 IP/MAC/Port mapping資料
     * @param model
     * @param request
     * @param response
     * @param queryGroup
     * @param queryIp
     * @param queryDateBegin
     * @param queryDateEnd
     * @param queryTimeBegin
     * @param queryTimeEnd
     * @return
     */
    @RequestMapping(value = "getMappingRecord.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse getMappingRecord(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="queryIp", required=false, defaultValue="") String queryIp,
            @RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
            @RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
            @RequestParam(name="queryTimeBegin", required=false, defaultValue="") String queryTimeBegin,
            @RequestParam(name="queryTimeEnd", required=false, defaultValue="") String queryTimeEnd) {

        try {

        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return null;
    }
}
