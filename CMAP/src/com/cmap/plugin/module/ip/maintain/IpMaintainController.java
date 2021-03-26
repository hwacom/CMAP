package com.cmap.plugin.module.ip.maintain;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
            
            behaviorLog(request.getRequestURI(), request.getQueryString());
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
        return "plugin/module_ip_maintain";
    }

    private String chkValues(int lineNum, String ipAddr, String macAddr, String ipDesc) {
        String retVal = null; // 回傳空值表示檢核正常

        // (1)檢核必填欄位
        if (StringUtils.isBlank(ipAddr)) {
            retVal = "第 " + lineNum + " 行資料檢核失敗!<br>錯誤原因: <font color='red'>IP_Address必填</font>";
            return retVal;

        } else if (StringUtils.isBlank(ipDesc)) {
            retVal = "第 " + lineNum + " 行資料檢核失敗!<br>錯誤原因: <font color='red'>IP備註必填</font>";
            return retVal;
        }

        // (2)檢核IP格式
        int ipDotCount = 0;
        char ch[] = ipAddr.toCharArray();
        for (char c : ch) {
            if (c == '.') {
                ipDotCount++;
            }
        }

        if (ipDotCount != 3) {
            retVal = "第 " + lineNum + " 行資料檢核失敗!<br>錯誤原因: <font color='red'>IP_Address格式錯誤</font>";
            return retVal;
        }

        // (3)檢核IP值
        String[] ipVal = ipAddr.split("\\.");
        for (String v : ipVal) {
            int value = Integer.parseInt(v);
            if (value < 0 || value > 255) {
                retVal = "第 " + lineNum + " 行資料檢核失敗!<br>錯誤原因: <font color='red'>IP_Address數值錯誤</font>";
                return retVal;
            }
        }

        // (4)若MAC有填值，檢核MAC格式
        if (StringUtils.isNotBlank(macAddr)) {
            int macColonCount = 0;
            ch = macAddr.toCharArray();
            for (char c : ch) {
                if (c == ':') {
                    macColonCount++;
                }
            }

            if (macColonCount != 5) {
                retVal = "第 " + lineNum + " 行資料檢核失敗!<br>錯誤原因: <font color='red'>MAC_Address格式錯誤</font>";
                return retVal;
            }
        }

        return retVal;
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public @ResponseBody AppResponse add(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            String groupId = jsonData.findValue(Constants.JSON_FIELD_GROUP_ID).asText();
            Iterator<JsonNode> ipAddrIt = jsonData.findValues(Constants.JSON_FIELD_IP_ADDR).get(0).iterator();
            Iterator<JsonNode> macAddrIt = jsonData.findValues(Constants.JSON_FIELD_MAC_ADDR).get(0).iterator();
            Iterator<JsonNode> ipDescIt = jsonData.findValues(Constants.JSON_FIELD_IP_DESC).get(0).iterator();

            IpMaintainServiceVO imsVO;
            String modifyIpAddr, modifyMacAddr, modifyIpDesc;
            int lineNum = 1;
            while (ipAddrIt.hasNext()) {
                modifyIpAddr = ipAddrIt.hasNext() ? ipAddrIt.next().asText() : null;
                modifyMacAddr = macAddrIt.hasNext() ? macAddrIt.next().asText() : null;
                modifyIpDesc = ipDescIt.hasNext() ? ipDescIt.next().asText() : null;

                String chkResult = chkValues(lineNum, modifyIpAddr, modifyMacAddr, modifyIpDesc);
                if (chkResult != null) {
                    return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, chkResult);
                }

                imsVO = new IpMaintainServiceVO();
                imsVO.setGroupId(groupId);
                imsVO.setModifyIpAddr(modifyIpAddr);
                imsVO.setModifyMacAddr(modifyMacAddr);
                imsVO.setModifyIpDesc(modifyIpDesc);

                imsVOs.add(imsVO);

                lineNum++;
            }

            ipMaintainService.addIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "新增成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        } finally {
        	behaviorLog(request.getRequestURI(), request.getQueryString());
        }
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public @ResponseBody AppResponse update(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            Iterator<JsonNode> groupIdIt = jsonData.findValues(Constants.JSON_FIELD_GROUP_IDS).get(0).iterator();
            Iterator<JsonNode> ipAddrIt = jsonData.findValues(Constants.JSON_FIELD_IP_ADDRS).get(0).iterator();
            Iterator<JsonNode> macAddrIt = jsonData.findValues(Constants.JSON_FIELD_MAC_ADDR).get(0).iterator();
            Iterator<JsonNode> ipDescIt = jsonData.findValues(Constants.JSON_FIELD_IP_DESC).get(0).iterator();

            IpMaintainServiceVO imsVO;
            String groupId, modifyIpAddr, modifyMacAddr, modifyIpDesc;
            int lineNum = 1;
            while (ipAddrIt.hasNext()) {
                groupId = groupIdIt.hasNext() ? groupIdIt.next().asText() : null;
                modifyIpAddr = ipAddrIt.hasNext() ? ipAddrIt.next().asText() : null;
                modifyMacAddr = macAddrIt.hasNext() ? macAddrIt.next().asText() : null;
                modifyIpDesc = ipDescIt.hasNext() ? ipDescIt.next().asText() : null;

                String chkResult = chkValues(lineNum, modifyIpAddr, modifyMacAddr, modifyIpDesc);
                if (chkResult != null) {
                    return new AppResponse(HttpServletResponse.SC_NOT_ACCEPTABLE, chkResult);
                }

                imsVO = new IpMaintainServiceVO();
                imsVO.setGroupId(groupId);
                imsVO.setIpAddr(modifyIpAddr);
                imsVO.setModifyMacAddr(modifyMacAddr);
                imsVO.setModifyIpDesc(modifyIpDesc);

                imsVOs.add(imsVO);

                lineNum++;
            }

            ipMaintainService.updateIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "修改成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        } finally {
        	behaviorLog(request.getRequestURI(), request.getQueryString());
        }
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public @ResponseBody AppResponse delete(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestBody JsonNode jsonData) {
        try {
            List<IpMaintainServiceVO> imsVOs = new ArrayList<>();

            Iterator<JsonNode> groupIdIt = jsonData.findValues(Constants.JSON_FIELD_GROUP_IDS).get(0).iterator();
            Iterator<JsonNode> ipAddrIt = jsonData.findValues(Constants.JSON_FIELD_IP_ADDRS).get(0).iterator();

            IpMaintainServiceVO imsVO;
            while (groupIdIt.hasNext()) {
                imsVO = new IpMaintainServiceVO();
                imsVO.setGroupId(groupIdIt.hasNext() ? groupIdIt.next().asText() : null);
                imsVO.setIpAddr(ipAddrIt.hasNext() ? ipAddrIt.next().asText() : null);
                imsVOs.add(imsVO);
            }

            ipMaintainService.deleteIpDataSetting(imsVOs);
            return new AppResponse(HttpServletResponse.SC_OK, "刪除成功");

        } catch (ServiceLayerException sle) {
            return new AppResponse(super.getLineNumber(), sle.getMessage());
        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(super.getLineNumber(), e.getMessage());
        } finally {
        	behaviorLog(request.getRequestURI(), request.getQueryString());
        }
    }

    @RequestMapping(value = "getIpDataSetting.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getIpDataSetting(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroup", required=true, defaultValue="") String queryGroup,
            @RequestParam(name="queryIp", required=false, defaultValue="") String queryIp,
            @RequestParam(name="queryMac", required=false, defaultValue="") String queryMac,
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
            ipmVO.setQueryMac(queryMac);
            ipmVO.setStartNum(startNum);
            ipmVO.setPageLength(pageLength);
            ipmVO.setSearchValue(searchValue);
            ipmVO.setOrderColumn(UI_COLUMNS[orderColIdx]);
            ipmVO.setOrderDirection(orderDirection);
            ipmVO.setIsSensorSearchMode(isSensorSearchMode);

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
        } finally {
        	behaviorLog(request.getRequestURI(), request.getQueryString());
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }
}
