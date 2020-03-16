package com.cmap.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.cmap.exception.ServiceLayerException;
import com.cmap.plugin.module.ip.blocked.record.IpBlockedRecordService;
import com.cmap.plugin.module.ip.blocked.record.IpBlockedRecordVO;
import com.cmap.plugin.module.ip.mac.bound.record.IpMacBoundRecordService;
import com.cmap.plugin.module.ip.mac.bound.record.IpMacBoundRecordVO;
import com.cmap.plugin.module.mac.blocked.record.MacBlockedRecordService;
import com.cmap.plugin.module.mac.blocked.record.MacBlockedRecordVO;
import com.cmap.plugin.module.port.blocked.record.PortBlockedRecordService;
import com.cmap.plugin.module.port.blocked.record.PortBlockedRecordVO;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeliveryService;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/delivery")
public class DeliveryController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_SEARCH_BY_SCRIPT_COLUMNS = new String[] {"","","scriptName","scriptType.scriptTypeName","systemVersion","","","",""};
	private static final String[] UI_RECORD_COLUMNS = new String[] {"","plm.begin_time","plm.create_by","dl.group_name","dl.device_name","dl.device_model","si.script_name","plm.remark","pls.result"};
	private static final String[] UI_BLOCKED_IP_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbil.ip_Address","mids.ip_Desc","mbil.status_Flag","mbil.block_Time","mbil.block_Reason","mbil.block_By","mbil.open_Time","mbil.open_Reason","mbil.open_By"};
	private static final String[] UI_BLOCKED_PORT_RECORD_COLUMNS = new String[] {"","","dl.groupName","dl.deviceName","mbpl.portId","mbpl.statusFlag","mbpl.blockTime","mbpl.blockReason","mbpl.blockBy","mbpl.openTime","mbpl.openReason","mbpl.openBy"};
	private static final String[] UI_BLOCKED_MAC_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbml.mac_Address","mbml.status_Flag","mbml.block_Time","mbml.block_Reason","mbml.block_By","mbml.open_Time","mbml.open_Reason","mbml.open_By"};
	private static final String[] UI_IP_MAC_BOUND_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbil.ip_Address","mids.ip_Desc","mids.mac_address","mids.port","mbil.status_Flag","mbil.block_Time","mbil.block_Reason","mbil.block_By","mbil.open_Time","mbil.open_Reason","mbil.open_By"};
	
	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private IpBlockedRecordService ipRecordService;
	
	@Autowired
	private IpMacBoundRecordService ipMacRecordService;

	@Autowired
    private PortBlockedRecordService portRecordService;

	@Autowired
	private MacBlockedRecordService macRecordService;
	
	private Map<String, String> groupListMap = null;
	private Map<String, String> deviceListMap = null;
	private Map<String, String> scriptTypeMap = null;

	private void initMenu(Model model, HttpServletRequest request) {
		try {
			groupListMap = getGroupList(request);
			scriptTypeMap = getScriptTypeList(Constants.DEFAULT_FLAG_N);

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			model.addAttribute("group", "");
			model.addAttribute("groupList", groupListMap);

			model.addAttribute("device", "");
			model.addAttribute("deviceList", deviceListMap);

			model.addAttribute("scriptType", "");
			model.addAttribute("scriptTypeList", scriptTypeMap);

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

		return "delivery/delivery_main";
	}

	@RequestMapping(value = "switchPort", method = RequestMethod.GET)
	public String switchPort(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_switch_port";
	}

	/**
     * 查找被封鎖過的Port紀錄
     * @param model
     * @param request
     * @param response
     * @param groupId
     * @param ipAddress
     * @param startNum
     * @param pageLength
     * @param searchValue
     * @param orderColIdx
     * @param orderDirection
     * @return
     */
    @RequestMapping(value = "getBlockedPortData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getBlockedPortData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=false, defaultValue="") String queryGroupId,
            @RequestParam(name="queryDeviceId", required=false, defaultValue="") String queryDeviceId,
            @RequestParam(name="queryPortId", required=false, defaultValue="") String queryPortId,
            @RequestParam(name="queryStatusFlag", required=false, defaultValue="") String queryStatusFlag,
            @RequestParam(name="queryBeginDate", required=false, defaultValue="") String queryBeginDate,
            @RequestParam(name="queryEndDate", required=false, defaultValue="") String queryEndDate,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<PortBlockedRecordVO> dataList = new ArrayList<>();
        PortBlockedRecordVO pbrVO;
        try {
            pbrVO = new PortBlockedRecordVO();
            pbrVO.setQueryGroupId(queryGroupId);
            pbrVO.setQueryDeviceId(queryDeviceId);

            setQueryGroupList(request, pbrVO, StringUtils.isNotBlank(queryGroupId) ? "queryGroupId" : "queryGroupIdList", queryGroupId);
            setQueryDeviceList(request, pbrVO, StringUtils.isNotBlank(queryDeviceId) ? "queryDeviceId" : "queryDeviceIdList", queryGroupId, queryDeviceId);

            //pbrVO.setQueryPortAddress(queryPortAddress);
            if (StringUtils.isNotBlank(queryStatusFlag)) {
                if(queryStatusFlag.equals(Constants.STATUS_FLAG_SYNC)) {
                	pbrVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
            	}else {
            		pbrVO.setQueryStatusFlag(Arrays.asList(queryStatusFlag));
            	}
            }
            pbrVO.setQueryBeginDate(queryBeginDate);
            pbrVO.setQueryEndDate(queryEndDate);
            pbrVO.setPageLength(pageLength);
            pbrVO.setSearchValue(searchValue);
            pbrVO.setOrderColumn(UI_BLOCKED_PORT_RECORD_COLUMNS[orderColIdx]);
            pbrVO.setOrderDirection(orderDirection);

            filterdTotal = portRecordService.countModuleBlockedPortList(pbrVO);

            if (filterdTotal != 0) {
                dataList = portRecordService.findModuleBlockedPortList(pbrVO, startNum, pageLength);
                
            }

            if(Constants.STATUS_FLAG_SYNC.equals(queryStatusFlag)) {
				boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
				String prtgLoginAccount = Objects
						.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

				if (deliveryService.doSyncDevicePortBlockedList(isAdmin, prtgLoginAccount, pbrVO, dataList)) {
					dataList = portRecordService.findModuleBlockedPortList(pbrVO, startNum, pageLength);
				}
				filterdTotal = dataList.size();
        	}
            
            total = filterdTotal;   //不顯示所有筆數，只顯示符合條件的筆數

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {

        } finally {
            //initMenu(model, request);
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }

	@RequestMapping(value = "ipOpenBlock", method = RequestMethod.GET)
	public String ipOpenBlock(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_ip_open_block";
	}

	@RequestMapping(value = "ipOpenBlock4Admin", method = RequestMethod.GET)
	public String ipOpenBlock4Admin(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_ip_open_block_4admin";
	}
	
	/**
	 * 查找被封鎖過的IP紀錄
	 * @param model
	 * @param request
	 * @param response
	 * @param groupId
	 * @param ipAddress
	 * @param startNum
	 * @param pageLength
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getBlockedIpData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getBlockedIpData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=false, defaultValue="") String queryGroupId,
            @RequestParam(name="queryDeviceId", required=false, defaultValue="") String queryDeviceId,
            @RequestParam(name="queryIpAddress", required=false, defaultValue="") String queryIpAddress,
            @RequestParam(name="queryStatusFlag", required=false, defaultValue="") String queryStatusFlag,
            @RequestParam(name="queryBeginDate", required=false, defaultValue="") String queryBeginDate,
            @RequestParam(name="queryEndDate", required=false, defaultValue="") String queryEndDate,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="5") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<IpBlockedRecordVO> dataList = new ArrayList<>();
        IpBlockedRecordVO irVO;
        try {
        	
            irVO = new IpBlockedRecordVO();

            setQueryGroupList(request, irVO, StringUtils.isNotBlank(queryGroupId) ? "queryGroupId" : "queryGroupIdList", queryGroupId);
            setQueryDeviceList(request, irVO, StringUtils.isNotBlank(queryDeviceId) ? "queryDeviceId" : "queryDeviceIdList", queryGroupId, queryDeviceId);

            irVO.setQueryIpAddress(queryIpAddress);
            
            if (StringUtils.isNotBlank(queryStatusFlag)) {
            	if(queryStatusFlag.equals(Constants.STATUS_FLAG_SYNC)) {
            		irVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
            	}else {
                    irVO.setQueryStatusFlag(Arrays.asList(queryStatusFlag));
            	}
            }
            irVO.setQueryBeginDate(queryBeginDate);
            irVO.setQueryEndDate(queryEndDate);
            irVO.setPageLength(pageLength);
            irVO.setSearchValue(searchValue);
            irVO.setOrderColumn(UI_BLOCKED_IP_RECORD_COLUMNS[orderColIdx]);
            irVO.setOrderDirection(orderDirection);

            boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
			irVO.setAdmin(isAdmin);
			
            filterdTotal = ipRecordService.countModuleBlockedIpList(irVO);

            if (filterdTotal != 0) {
                dataList = ipRecordService.findModuleBlockedIpList(irVO, startNum, pageLength);
                
            }

            if(Constants.STATUS_FLAG_SYNC.equals(queryStatusFlag) && StringUtils.isNotBlank(irVO.getQueryGroupId())) {
            	
				String prtgLoginAccount = Objects
						.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

				if (deliveryService.doSyncDeviceIpBlockedList(isAdmin, prtgLoginAccount, irVO, dataList)) {
					dataList = ipRecordService.findModuleBlockedIpList(irVO, startNum, pageLength);
				}
				filterdTotal = dataList.size();
        	}
            
            total = filterdTotal;   //不顯示所有筆數，只顯示符合條件的筆數

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {

        } finally {
            //initMenu(model, request);
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }

	@RequestMapping(value = "ipMacBinding", method = RequestMethod.GET)
	public String ipMacBinding(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_ip_mac_binding";
	}
	/**
	 * 查找被封鎖過的IP紀錄
	 * @param model
	 * @param request
	 * @param response
	 * @param groupId
	 * @param ipAddress
	 * @param startNum
	 * @param pageLength
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getIpMacBoundData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getIpMacBoundData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=false, defaultValue="") String queryGroupId,
            @RequestParam(name="queryDeviceId", required=false, defaultValue="") String queryDeviceId,
            @RequestParam(name="queryIpAddress", required=false, defaultValue="") String queryIpAddress,
            @RequestParam(name="queryMacAddress", required=false, defaultValue="") String queryMacAddress,
            @RequestParam(name="queryStatusFlag", required=false, defaultValue="") String queryStatusFlag,
            @RequestParam(name="queryBeginDate", required=false, defaultValue="") String queryBeginDate,
            @RequestParam(name="queryEndDate", required=false, defaultValue="") String queryEndDate,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="5") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<IpMacBoundRecordVO> dataList = new ArrayList<>();
        IpMacBoundRecordVO irVO;
        try {
        	
            irVO = new IpMacBoundRecordVO();

            setQueryGroupList(request, irVO, StringUtils.isNotBlank(queryGroupId) ? "queryGroupId" : "queryGroupIdList", queryGroupId);
            setQueryDeviceList(request, irVO, StringUtils.isNotBlank(queryDeviceId) ? "queryDeviceId" : "queryDeviceIdList", queryGroupId, queryDeviceId);

            irVO.setQueryIpAddress(queryIpAddress);
            if (StringUtils.isNotBlank(queryMacAddress)) {
            	irVO.setQueryMacAddress(queryMacAddress);
	        }
            if (StringUtils.isNotBlank(queryStatusFlag)) {
            	irVO.setQueryStatusFlag(Arrays.asList(queryStatusFlag));
            }
            irVO.setQueryBeginDate(queryBeginDate);
            irVO.setQueryEndDate(queryEndDate);
            irVO.setPageLength(pageLength);
            irVO.setSearchValue(searchValue);
            irVO.setOrderColumn(UI_IP_MAC_BOUND_RECORD_COLUMNS[orderColIdx]);
            irVO.setOrderDirection(orderDirection);

            boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
			irVO.setAdmin(isAdmin);
			
            filterdTotal = ipMacRecordService.countModuleIpMacBoundList(irVO);

            if (filterdTotal != 0) {
                dataList = ipMacRecordService.findModuleIpMacBoundList(irVO, startNum, pageLength);
                
            }
            
            total = filterdTotal;   //不顯示所有筆數，只顯示符合條件的筆數

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {

        } finally {
            //initMenu(model, request);
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }
	
	@RequestMapping(value = "macOpenBlock", method = RequestMethod.GET)
	public String macOpenBlock(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "plugin/module_mac_open_block";
	}

	/**
	 * 查找被封鎖過的Mac紀錄
	 * @param model
	 * @param request
	 * @param response
	 * @param groupId
	 * @param macAddress
	 * @param startNum
	 * @param pageLength
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getBlockedMacData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getBlockedMacData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=false, defaultValue="") String queryGroupId,
            @RequestParam(name="queryDeviceId", required=false, defaultValue="") String queryDeviceId,
            @RequestParam(name="queryMacAddress", required=false, defaultValue="") String queryMacAddress,
            @RequestParam(name="queryStatusFlag", required=false, defaultValue="") String queryStatusFlag,
            @RequestParam(name="queryBeginDate", required=false, defaultValue="") String queryBeginDate,
            @RequestParam(name="queryEndDate", required=false, defaultValue="") String queryEndDate,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="5") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<MacBlockedRecordVO> dataList = new ArrayList<>();
        MacBlockedRecordVO mrVO;
        try {
            mrVO = new MacBlockedRecordVO();

            setQueryGroupList(request, mrVO, StringUtils.isNotBlank(queryGroupId) ? "queryGroupId" : "queryGroupIdList", queryGroupId);
            setQueryDeviceList(request, mrVO, StringUtils.isNotBlank(queryDeviceId) ? "queryDeviceId" : "queryDeviceIdList", queryGroupId, queryDeviceId);

            mrVO.setQueryMacAddress(queryMacAddress);
            if (StringUtils.isNotBlank(queryStatusFlag)) {
                if(queryStatusFlag.equals(Constants.STATUS_FLAG_SYNC)) {
                	mrVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
            	}else {
            		mrVO.setQueryStatusFlag(Arrays.asList(queryStatusFlag));
            	}
            }
            mrVO.setQueryBeginDate(queryBeginDate);
            mrVO.setQueryEndDate(queryEndDate);
            mrVO.setPageLength(pageLength);
            mrVO.setSearchValue(searchValue);
            mrVO.setOrderColumn(UI_BLOCKED_MAC_RECORD_COLUMNS[orderColIdx]);
            mrVO.setOrderDirection(orderDirection);

            filterdTotal = macRecordService.countModuleBlockedMacList(mrVO);

            if (filterdTotal != 0) {
                dataList = macRecordService.findModuleBlockedMacList(mrVO, startNum, pageLength);
                
            }

            if(Constants.STATUS_FLAG_SYNC.equals(queryStatusFlag)) {
				boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
				String prtgLoginAccount = Objects
						.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

				if (deliveryService.doSyncDeviceMacBlockedList(isAdmin, prtgLoginAccount, mrVO, dataList)) {
					dataList = macRecordService.findModuleBlockedMacList(mrVO, startNum, pageLength);
				}
				filterdTotal = dataList.size();
        	}
            
            total = filterdTotal;   //不顯示所有筆數，只顯示符合條件的筆數

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {

        } finally {
            //initMenu(model, request);
        }

        return new DatatableResponse(total, dataList, filterdTotal);
    }
	
	@RequestMapping(value = "record", method = RequestMethod.GET)
	public String record(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {


		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}

		return "delivery/delivery_record";
	}

	@RequestMapping(value = "getDeviceListData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse queryByDevice(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<DeliveryServiceVO> dataList = new ArrayList<>();
		DeliveryServiceVO dsVO;
		try {
			dsVO = new DeliveryServiceVO();

		} catch (Exception e) {

		} finally {
			//initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	@RequestMapping(value = "getScriptListData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse queryByScript(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryScriptTypeCode", required=false, defaultValue="") String queryScriptTypeCode,
			@RequestParam(name="onlyOneScript", required=false, defaultValue="") String onlyOneScript,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="25") Integer pageLength,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="2") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="asc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<DeliveryServiceVO> dataList = new ArrayList<>();
		DeliveryServiceVO dsVO;
		try {
			dsVO = new DeliveryServiceVO();
			dsVO.setQueryScriptTypeCode(queryScriptTypeCode);
			dsVO.setStartNum(startNum);
			dsVO.setPageLength(pageLength);
			dsVO.setSearchValue(searchValue);
			dsVO.setOrderColumn(UI_SEARCH_BY_SCRIPT_COLUMNS[orderColIdx]);
			dsVO.setOrderDirection(orderDirection);
			
			try {
				boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
				dsVO.setAdmin(isAdmin);
				
			} catch (Exception e) {
				log.error(e.toString(), e);
			}

			switch (onlyOneScript) {
				case Constants.DELIVERY_ONLY_SCRIPT_OF_SWITCH_PORT:
					dsVO.setOnlySwitchPort(true);
					break;

				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_OPEN_BLOCK:
					dsVO.setOnlyIpOpenBlock(true);
					break;

				case Constants.DELIVERY_ONLY_SCRIPT_OF_MAC_OPEN_BLOCK:
					dsVO.setOnlyMacOpenBlock(true);
					break;
					
				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_MAC_BINDING:
					dsVO.setOnlyIpMacBinding(true);
					break;					
			}

			filterdTotal = deliveryService.countScriptList(dsVO);

			if (filterdTotal != 0) {
				dataList = deliveryService.findScriptList(dsVO, startNum, pageLength);
			}

			total = deliveryService.countScriptList(new DeliveryServiceVO());

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	private boolean logAccessRecord(HttpServletRequest request, DeliveryServiceVO logVO) {
		try {
			String ipAddr = getIp(request);
			String macAddr = getMac(ipAddr);
			logVO.setIpAddr(ipAddr);
			logVO.setMacAddr(macAddr);
			logVO.setActionBy(SecurityUtil.getSecurityUser().getUsername());
			logVO.setActionTime(new Date());
			deliveryService.logAccessRecord(logVO);
			return true;

		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}
	}

	@RequestMapping(value = "getScriptInfo.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse getScriptInfo(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="scriptInfoId", required=true) String scriptInfoId) {

		DeliveryServiceVO dsVO;
		try {
			dsVO = deliveryService.getScriptInfoByIdOrCode(scriptInfoId, null);
			final String deviceModel = dsVO.getDeviceModel();

			//取得Group & Device選單內容
			Map<String, String> menuMap = getGroupDeviceMenu(request, null, deviceModel);
			dsVO.setGroupDeviceMenuJsonStr(new Gson().toJson(menuMap));

			ObjectMapper oMapper = new ObjectMapper();
			Map<String, Object> retMap = oMapper.convertValue(dsVO, Map.class);
			retMap.put("deviceModel", deviceModel);

			return new AppResponse(HttpServletResponse.SC_OK, "資料取得正常", retMap);

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
		}
	}

	@RequestMapping(value = "getVariableSetting.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse getVariableSetting(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="groupArray", required=true) String groupArray,
			@RequestParam(name="deviceArray", required=true) String deviceArray,
			@RequestParam(name="varKeyArray", required=true) String varKeyArray) {

		DeliveryServiceVO dsVO;
		try {
			ObjectMapper oMapper = new ObjectMapper();
			List<String> groups = oMapper.readValue(groupArray, List.class);
			List<String> devices = oMapper.readValue(deviceArray, List.class);
			List<String> variables = oMapper.readValue(varKeyArray, List.class);

			dsVO = deliveryService.getVariableSetting(groups, devices, variables);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "資料取得正常");
			app.putData("info", dsVO.getDeviceVarMap());
			app.putData("symbol", Env.COMM_SEPARATE_SYMBOL);

			return app;

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "資料取得異常");
		}
	}

	@RequestMapping(value = "doDelivery.json", method = RequestMethod.POST, produces="application/json;odata=verbose")
	public @ResponseBody AppResponse doDelivery(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="ps", required=true) String ps) {

		DeliveryServiceVO retVO;
		try {
			DeliveryParameterVO pVO = (DeliveryParameterVO)transJSON2Object(ps, DeliveryParameterVO.class);
			
			boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
			
			pVO = deliveryService.checkB4DoBindingDelivery(isAdmin, pVO);
			
			retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, pVO, false, null, null, true);
			String retVal = retVO.getRetMsg();

			return new AppResponse(HttpServletResponse.SC_OK, retVal);

		} catch (ServiceLayerException sle) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());

		} catch (Exception e) {
			log.error(e.toString(), e);
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * 執行IP開通 by 「IP開通/封鎖」功能中的「解鎖」按鈕
	 * @param model
	 * @param request
	 * @param response
	 * @param listId
	 * @return
	 */
	@RequestMapping(value = "doIpOpenByBtn.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse doIpOpenByBtn(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="listId[]", required=true) String[] listIdArray,
            @RequestParam(name="reason", required=false) String reasonInput) {

	    DeliveryServiceVO retVO = null;
	    DeliveryParameterVO pVO;
        try {
            pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

//            String scriptInfoId = Env.DEFAULT_IP_OPEN_SCRIPT_INFO_ID;
            String scriptCode = null;
            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            String[] scriptCodeArr;
            
            // Step 1. 準備必要參數
            IpBlockedRecordVO ibrVO;
            List<IpBlockedRecordVO> recordList = null;
            boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
            
            for (String listId : listIdArray) {
                ibrVO = new IpBlockedRecordVO();
                ibrVO.setQueryListId(listId);
                ibrVO.setAdmin(isAdmin);
                
                recordList = ipRecordService.findModuleBlockedIpList(ibrVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    ibrVO = recordList.get(0);
                    scriptCodeArr = ibrVO.getScriptCode().split("_");
                    scriptCode = scriptCodeArr[0]+"_"+ String.format("%03d",Integer.parseInt(scriptCodeArr[1])+1);
                    		
                    // Step 2. 查解鎖腳本
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, scriptCode);

                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
                    
                    groupId = ibrVO.getGroupId();
                    deviceId = ibrVO.getDeviceId();

                    varValue = new ArrayList<>();
                    varValue.add(ibrVO.getIpAddress()); // IP_ADDRESS

                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }
            }

            // Step 3. 呼叫共用
            pVO.setScriptInfoId(retVO.getScriptInfoId());
            pVO.setScriptCode(scriptCode);
            pVO.setGroupId(groupIds);
            pVO.setDeviceId(deviceIds);
            pVO.setVarKey(varKeys);
            pVO.setVarValue(varValues);
            pVO.setReason(reason);

            retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, pVO, false, null, null, true);
            String retVal = retVO.getRetMsg();

            return new AppResponse(HttpServletResponse.SC_OK, retVal);

        } catch (ServiceLayerException sle) {
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

	/**
	 * 執行Mac開通 by 「Mac開通/封鎖」功能中的「解鎖」按鈕
	 * @param model
	 * @param request
	 * @param response
	 * @param listId
	 * @return
	 */
	@RequestMapping(value = "doMacOpenByBtn.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse doMacOpenByBtn(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="listId[]", required=true) String[] listIdArray,
            @RequestParam(name="reason", required=false) String reasonInput) {

	    DeliveryServiceVO retVO;
	    DeliveryParameterVO pVO;
        try {
            pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

            String scriptInfoId = Env.DEFAULT_MAC_OPEN_SCRIPT_INFO_ID;
            String scriptCode = null;
            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;

            // Step 1. 查解鎖腳本
            retVO = deliveryService.getScriptInfoByIdOrCode(scriptInfoId, null);

            scriptCode = retVO.getScriptCode();
            varKeyJson = retVO.getActionScriptVariable();
            Gson gson = new Gson();
            varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());

            // Step 2. 準備必要參數
            MacBlockedRecordVO mbrVO;
            List<MacBlockedRecordVO> recordList = null;
            for (String listId : listIdArray) {
                mbrVO = new MacBlockedRecordVO();
                mbrVO.setQueryListId(listId);

                recordList = macRecordService.findModuleBlockedMacList(mbrVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    mbrVO = recordList.get(0);

                    groupId = mbrVO.getGroupId();
                    deviceId = mbrVO.getDeviceId();

                    varValue = new ArrayList<>();
                    varValue.add(mbrVO.getMacAddress()); // IP_ADDRESS

                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }
            }

            // Step 3. 呼叫共用
            pVO.setScriptInfoId(scriptInfoId);
            pVO.setScriptCode(scriptCode);
            pVO.setGroupId(groupIds);
            pVO.setDeviceId(deviceIds);
            pVO.setVarKey(varKeys);
            pVO.setVarValue(varValues);
            pVO.setReason(reason);

            retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, pVO, false, null, null, true);
            String retVal = retVO.getRetMsg();

            return new AppResponse(HttpServletResponse.SC_OK, retVal);

        } catch (ServiceLayerException sle) {
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

	/**
     * 執行Port開通 by 「開/關Switch Port」功能中的「解鎖」按鈕
     * @param model
     * @param request
     * @param response
     * @param listId
     * @return
     */
    @RequestMapping(value = "doPortOpenByBtn.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse doPortOpenByBtn(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="listId[]", required=true) String[] listIdArray,
            @RequestParam(name="reason", required=false) String reasonInput) {

        DeliveryServiceVO retVO;
        DeliveryParameterVO pVO;
        try {
            pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

            String scriptInfoId = Env.DEFAULT_SWITCH_PORT_OPEN_SCRIPT_INFO_ID;
            String scriptCode = null;
            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;

            // Step 1. 查解鎖腳本
            retVO = deliveryService.getScriptInfoByIdOrCode(scriptInfoId, null);

            scriptCode = retVO.getScriptCode();
            varKeyJson = retVO.getActionScriptVariable();
            Gson gson = new Gson();
            varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());

            // Step 2. 準備必要參數
            PortBlockedRecordVO pbrVO;
            List<PortBlockedRecordVO> recordList = null;
            for (String listId : listIdArray) {
                pbrVO = new PortBlockedRecordVO();
                pbrVO.setQueryListId(listId);

                recordList = portRecordService.findModuleBlockedPortList(pbrVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    pbrVO = recordList.get(0);

                    groupId = pbrVO.getGroupId();
                    deviceId = pbrVO.getDeviceId();

                    varValue = new ArrayList<>();
                    varValue.add(pbrVO.getPortId()); // PORT_ID

                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }
            }

            // Step 3. 呼叫共用
            pVO.setScriptInfoId(scriptInfoId);
            pVO.setScriptCode(scriptCode);
            pVO.setGroupId(groupIds);
            pVO.setDeviceId(deviceIds);
            pVO.setVarKey(varKeys);
            pVO.setVarValue(varValues);
            pVO.setReason(reason);

            retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, pVO, false, null, null, true);
            String retVal = retVO.getRetMsg();

            return new AppResponse(HttpServletResponse.SC_OK, retVal);

        } catch (ServiceLayerException sle) {
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    /**
	 * 執行IP MAC解鎖 by 「IP MAC綁定」功能中的「解除綁定」按鈕
	 * @param model
	 * @param request
	 * @param response
	 * @param listId
	 * @return
	 */
	@RequestMapping(value = "doIpMacUnbindByBtn.json", method = RequestMethod.POST)
    public @ResponseBody AppResponse doIpMacUnbindByBtn(Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="listId[]", required=true) String[] listIdArray,
            @RequestParam(name="reason", required=false) String reasonInput) {

	    DeliveryServiceVO retVO = null;
	    DeliveryParameterVO pVO;
        try {
            pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

            String scriptCode = null;
            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            String[] scriptCodeArr;
            
            // Step 1. 準備必要參數
            IpMacBoundRecordVO queryVO, ibrVO;
            List<IpMacBoundRecordVO> recordList = null;  
            Map<String, Integer>recordPortMap = null;
            for (String listId : listIdArray) {
            	queryVO = new IpMacBoundRecordVO();
            	queryVO.setQueryListId(listId);
                
                recordList = ipMacRecordService.findModuleIpMacBoundList(queryVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    ibrVO = recordList.get(0);
                    scriptCodeArr = ibrVO.getScriptCode().split("_");
                    scriptCode = scriptCodeArr[0]+"_"+ String.format("%03d",Integer.parseInt(scriptCodeArr[1])+1);
                    		
                    // Step 2. 查解鎖腳本
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, scriptCode);

                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
                    
                    groupId = ibrVO.getGroupId();
                    deviceId = ibrVO.getDeviceId();

                    queryVO = new IpMacBoundRecordVO();
                	queryVO.setQueryDeviceId(deviceId);
                    queryVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
                    
                    recordList = ipMacRecordService.findModuleIpMacBoundList(queryVO, null, null);
                    recordPortMap = new HashMap<String, Integer>();
                    for (IpMacBoundRecordVO ipVO : recordList) {
                    	if(recordPortMap.containsKey(ipVO.getPort())) {
                    		recordPortMap.put(ipVO.getPort(), recordPortMap.get(ipVO.getPort())+1);
                    	}else {
                    		recordPortMap.put(ipVO.getPort(), 1);
                    	}
					}
                    
                    varValue = new ArrayList<>();
                    for (String key : varKeys) {
						if(compareStringNotBlankAndEqualsIgnoreCase(Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK, key)) {
							varValue.add(ibrVO.getIpAddress()); // IP_ADDRESS
							
						} else if(compareStringNotBlankAndEqualsIgnoreCase(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK, key)) {
							varValue.add(ibrVO.getMacAddress()); // MAC_ADDRESS
							
						} else if(compareStringNotBlankAndEqualsIgnoreCase(Env.KEY_VAL_OF_NO_FLAG_WITH_CMD, key)) {
							if(ibrVO.getGlobalValue().equalsIgnoreCase(ibrVO.getPort()) && recordPortMap.get(ibrVO.getPort()) == 1) {
								varValue.add("no");
							}else {
								varValue.add("");
							}
							
						} else if(compareStringNotBlankAndEqualsIgnoreCase(Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING, key)) {
							if(!ibrVO.getGlobalValue().equalsIgnoreCase(ibrVO.getPort()) && recordPortMap.get(ibrVO.getPort()) == 1) {
								String globalValue = ibrVO.getGlobalValue().replaceAll(ibrVO.getPort()+",", "");
	                			globalValue = globalValue.replaceAll(","+ibrVO.getPort()+",", "");
	                			globalValue = globalValue.replaceAll(","+ibrVO.getPort(), "");
								varValue.add(globalValue);
							}else {
								varValue.add(ibrVO.getGlobalValue());
							}
						}
					}
                    
                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }else {
                	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無綁定紀錄無法解除綁定");
                }
            }

            // Step 3. 呼叫共用
            pVO.setScriptInfoId(retVO.getScriptInfoId());
            pVO.setScriptCode(scriptCode);
            pVO.setGroupId(groupIds);
            pVO.setDeviceId(deviceIds);
            pVO.setVarKey(varKeys);
            pVO.setVarValue(varValues);
            pVO.setReason(reason);

            retVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, pVO, false, null, null, true);
            String retVal = retVO.getRetMsg();

            return new AppResponse(HttpServletResponse.SC_OK, retVal);

        } catch (ServiceLayerException sle) {
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, sle.getMessage());

        } catch (Exception e) {
            log.error(e.toString(), e);
            return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
	
	private boolean compareStringNotBlankAndEqualsIgnoreCase(String str1, String str2) {
		return StringUtils.isNotBlank(str1) && StringUtils.equalsIgnoreCase(str1, str2);
	}
	
	@RequestMapping(value = "getDeliveryRecordData.json", method = RequestMethod.POST)
	public @ResponseBody DatatableResponse getDeliveryRecordData(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="queryGroup", required=false, defaultValue="") String queryGroup,
			@RequestParam(name="queryDevice", required=false, defaultValue="") String queryDevice,
			@RequestParam(name="queryDateBegin", required=false, defaultValue="") String queryDateBegin,
			@RequestParam(name="queryDateEnd", required=false, defaultValue="") String queryDateEnd,
			@RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
			@RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
			@RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
			@RequestParam(name="order[0][column]", required=false, defaultValue="6") Integer orderColIdx,
			@RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

		long total = 0;
		long filterdTotal = 0;
		List<DeliveryServiceVO> dataList = new ArrayList<>();
		DeliveryServiceVO dsVO;
		try {
			dsVO = new DeliveryServiceVO();
			dsVO.setQueryGroup(queryGroup);
			dsVO.setQueryDevice(queryDevice);
			dsVO.setQueryTimeBegin(queryDateBegin);
			dsVO.setQueryTimeEnd(queryDateEnd);
			dsVO.setStartNum(startNum);
			dsVO.setPageLength(pageLength);
			dsVO.setSearchValue(searchValue);
			dsVO.setOrderColumn(UI_RECORD_COLUMNS[orderColIdx]);
			dsVO.setOrderDirection(orderDirection);

			filterdTotal = deliveryService.countProvisionLog(dsVO);

			if (filterdTotal != 0) {
				dataList = deliveryService.findProvisionLog(dsVO, startNum, pageLength);
			}

			total = deliveryService.countProvisionLog(new DeliveryServiceVO());

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {

		} finally {
			//initMenu(model, request);
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}

	@RequestMapping(value = "viewProvisionLog.json", method = RequestMethod.POST)
	public @ResponseBody AppResponse viewProvisionLog(
			Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name="logStepId", required=true) String logStepId) {
		try {
			DeliveryServiceVO dsVO = new DeliveryServiceVO();
			dsVO.setQueryLogStepId(logStepId);

			dsVO = deliveryService.getProvisionLogById(logStepId);

			AppResponse app = new AppResponse(HttpServletResponse.SC_OK, "資料取得正常");
			app.putData("log", dsVO.getProvisionLog());

			return app;

		} catch (Exception e) {
			return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查找供裝紀錄發生錯誤，請重新操作");

		} finally {
			//initMenu(model, request);
		}
	}
	
}
