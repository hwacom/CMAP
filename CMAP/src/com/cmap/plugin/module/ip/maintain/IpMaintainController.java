package com.cmap.plugin.module.ip.maintain;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import com.cmap.DatatableResponse;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/plugin/module/ipMaintain")
public class IpMaintainController {
    @Log
    private static Logger log;

    @Autowired
    private IpMaintainService ipMaintainService;

    private static final String[] UI_COLUMNS = new String[] {"","Create_Time","Group_Name","Device_Name","","Ip_Address","Mac_Address","Port"};

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String main(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        try {


        } catch (Exception e) {
            log.error(e.toString(), e);

        } finally {
        }
        return "plugin/module_ip_data_setting";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody AppResponse add(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {

        return null;
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody AppResponse update(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {

        return null;
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public @ResponseBody AppResponse delete(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {

        return null;
    }

    @RequestMapping(value = "getIpDataSetting.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getIpDataSetting(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="queryIp", required=false, defaultValue="") String queryIp,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="100") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<IpMaintainServiceVO> dataList = new ArrayList<>();
        IpMaintainServiceVO ipmVO = null;
        try {
            ipmVO = new IpMaintainServiceVO();
            ipmVO.setQueryGroup(queryGroup);
            ipmVO.setQueryIp(queryIp);
            ipmVO.setStartNum(startNum);
            ipmVO.setPageLength(pageLength);
            ipmVO.setSearchValue(searchValue);
            ipmVO.setOrderColumn(UI_COLUMNS[orderColIdx]);
            ipmVO.setOrderDirection(orderDirection);

            filterdTotal = ipMaintainService.countIpDataSetting(ipmVO);

            if (filterdTotal != 0) {
                dataList = ipMaintainService.findIpDataSetting(ipmVO);
            }

            ipmVO = new IpMaintainServiceVO();
            ipmVO.setQueryGroup(queryGroup);

            total = ipMaintainService.countIpDataSetting(ipmVO);

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {
            log.error(e.toString(), e);
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }
}
