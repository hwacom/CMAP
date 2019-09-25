package com.cmap.plugin.module.ip.maintain;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.cmap.Constants;
import com.cmap.DatatableResponse;
import com.cmap.annotation.Log;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.security.SecurityUtil;
import com.fasterxml.jackson.databind.JsonNode;

@Controller
@RequestMapping("/plugin/module/ipMaintain")
public class IpMaintainController extends BaseController {
    @Log
    private static Logger log;

    @Autowired
    private IpMaintainService ipMaintainService;

    private static final String[] UI_COLUMNS = new String[] {"","","dl.groupName","mids.ipAddr","mids.ipDesc"};

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
        return "plugin/module_ip_data_setting";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody AppResponse add(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            String groupId = jsonData.findValue(Constants.JSON_FIELD_GROUP_ID).asText();
            Iterator<JsonNode> ipAddrIt = jsonData.findValues(Constants.JSON_FIELD_IP_ADDR).get(0).iterator();
            Iterator<JsonNode> ipDescIt = jsonData.findValues(Constants.JSON_FIELD_IP_DESC).get(0).iterator();

            IpMaintainServiceVO imsVO;
            while (ipAddrIt.hasNext()) {
                imsVO = new IpMaintainServiceVO();
                imsVO.setGroupId(groupId);
                imsVO.setModifyIpAddr(ipAddrIt.hasNext() ? ipAddrIt.next().asText() : null);
                imsVO.setModifyIpDesc(ipDescIt.hasNext() ? ipDescIt.next().asText() : null);

                imsVOs.add(imsVO);
            }

            ipMaintainService.addIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "新增成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody AppResponse update(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_SETTING_IDS).get(0).iterator();
            Iterator<JsonNode> ipDescIt = jsonData.findValues(Constants.JSON_FIELD_IP_DESC).get(0).iterator();

            IpMaintainServiceVO imsVO;
            while (idIt.hasNext()) {
                imsVO = new IpMaintainServiceVO();
                imsVO.setSettingId(idIt.hasNext() ? idIt.next().asText() : null);
                imsVO.setModifyIpDesc(ipDescIt.hasNext() ? ipDescIt.next().asText() : null);

                imsVOs.add(imsVO);
            }

            ipMaintainService.updateIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "修改成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public @ResponseBody AppResponse delete(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            Iterator<JsonNode> idIt = jsonData.findValues(Constants.JSON_FIELD_SETTING_IDS).get(0).iterator();

            IpMaintainServiceVO imsVO;
            while (idIt.hasNext()) {
                imsVO = new IpMaintainServiceVO();
                imsVO.setSettingId(idIt.hasNext() ? idIt.next().asText() : null);
                imsVOs.add(imsVO);
            }

            ipMaintainService.deleteIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "刪除成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        }
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
