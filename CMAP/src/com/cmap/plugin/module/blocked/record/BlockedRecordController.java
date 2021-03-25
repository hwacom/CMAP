package com.cmap.plugin.module.blocked.record;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cmap.comm.enums.BlockType;
import com.cmap.comm.enums.ScriptType;
import com.cmap.controller.BaseController;
import com.cmap.exception.ServiceLayerException;
import com.cmap.security.SecurityUtil;
import com.cmap.service.DeliveryService;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Controller
@RequestMapping("/plugin/module/blockedRecord")
public class BlockedRecordController extends BaseController {
	@Log
	private static Logger log;

	private static final String[] UI_SEARCH_BY_SCRIPT_COLUMNS = new String[] {"","","scriptName","scriptType.scriptTypeName","deviceModel","","","",""};
	private static final String[] UI_BLOCKED_IP_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbl.ip_Address","mids.ip_Desc","mbl.status_Flag","mbl.block_Time","mbl.block_Reason","mbl.block_By","mbl.open_Time","mbl.open_Reason","mbl.open_By"};
	private static final String[] UI_BLOCKED_PORT_RECORD_COLUMNS = new String[] {"","","dl.group_Name","dl.device_Name","mbl.port_Id","mbl.status_Flag","mbl.block_Time","mbl.block_Reason","mbl.block_By","mbl.open_Time","mbl.open_Reason","mbl.open_By"};
	private static final String[] UI_BLOCKED_MAC_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbl.mac_Address","mbl.status_Flag","mbl.block_Time","mbl.block_Reason","mbl.block_By","mbl.open_Time","mbl.open_Reason","mbl.open_By"};
	private static final String[] UI_IP_MAC_BOUND_RECORD_COLUMNS = new String[] {"","","dl.group_Name","mbl.ip_Address","mids.ip_Desc","mids.mac_address","mids.port","mbl.status_Flag","mbl.block_Time","mbl.block_Reason","mbl.block_By","mbl.open_Time","mbl.open_Reason","mbl.open_By"};
	private boolean showSyncAction = StringUtils.equalsIgnoreCase(Env.SHOW_SYNC_SWITCH_RECORD_ACTION, Constants.DATA_Y);
	
	@Autowired
	private BlockedRecordService blockedRecordService;
	
	@Autowired
	private DeliveryService deliveryService;
	
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

			model.addAttribute("showSyncAction", showSyncAction);
			
			model.addAttribute("device", "");
			model.addAttribute("deviceList", deviceListMap);

			model.addAttribute("scriptType", "");
			model.addAttribute("scriptTypeList", scriptTypeMap);

			model.addAttribute("userInfo", SecurityUtil.getSecurityUser().getUsername());
			
			behaviorLog(request.getRequestURI(), request.getQueryString());
		}
	}

	@RequestMapping(value = "ipMacBinding", method = RequestMethod.GET)
	public String ipRecord(Model model, Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {

		} catch (Exception e) {
			log.error(e.toString(), e);

		} finally {
			initMenu(model, request);
		}
		return "plugin/module_ip_mac_binding";
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

            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            
            // Step 1. 準備必要參數
            BlockedRecordVO queryVO, brVO;
            List<BlockedRecordVO> recordList = null;  
            Map<String, Integer>recordPortMap = null;
            for (String listId : listIdArray) {
            	queryVO = new BlockedRecordVO();
            	queryVO.setQueryBlockType(BlockType.BIND.toString());
            	queryVO.setQueryListId(listId);
                
                recordList = blockedRecordService.findModuleBlockedList(queryVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    brVO = recordList.get(0);
                    if(StringUtils.isBlank(brVO.getUndoScriptCode())) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    // Step 2. 查解鎖腳本
                    boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, brVO.getUndoScriptCode(), isAdmin);
                    if(retVO == null) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    pVO.setScriptCode(brVO.getUndoScriptCode());
                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
        			if(StringUtils.isNotBlank(retVO.getCheckScriptVariable())) {
        				gson = new Gson();
                        varKeys.addAll(gson.fromJson(retVO.getCheckScriptVariable(), new TypeToken<List<String>>(){}.getType()));
        			}
        			
                    groupId = brVO.getGroupId();
                    deviceId = brVO.getDeviceId();

                    queryVO = new BlockedRecordVO();
                	queryVO.setQueryDeviceId(deviceId);
                    queryVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
                    
                    recordList = blockedRecordService.findModuleBlockedList(queryVO, null, null);
                    recordPortMap = new HashMap<String, Integer>();
                    for (BlockedRecordVO ipVO : recordList) {
                    	if(recordPortMap.containsKey(ipVO.getPort())) {
                    		recordPortMap.put(ipVO.getPort(), recordPortMap.get(ipVO.getPort())+1);
                    	}else {
                    		recordPortMap.put(ipVO.getPort(), 1);
                    	}
					}
                    
                    varValue = new ArrayList<>();
                    for (String key : varKeys) {
						if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getIpAddress()); // IP_ADDRESS
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getMacAddress()); // MAC_ADDRESS
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_NO_FLAG_WITH_CMD.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							if(brVO.getGlobalValue().equalsIgnoreCase(brVO.getPort()) && recordPortMap.get(brVO.getPort()) == 1) {
								varValue.add("no");
							}else {
								varValue.add("");
							}
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							if(!brVO.getGlobalValue().equalsIgnoreCase(brVO.getPort()) && recordPortMap.get(brVO.getPort()) == 1) {
								String globalValue = brVO.getGlobalValue().replaceAll(brVO.getPort()+",", "");
	                			globalValue = globalValue.replaceAll(","+brVO.getPort()+",", "");
	                			globalValue = globalValue.replaceAll(","+brVO.getPort(), "");
								varValue.add(globalValue);
							}else {
								varValue.add(brVO.getGlobalValue());
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
        } finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
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

	    DeliveryServiceVO retVO = null;
	    DeliveryParameterVO pVO;
        try {
            pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            
            // Step 1. 準備必要參數
            BlockedRecordVO queryVO, brVO;
            List<BlockedRecordVO> recordList = null;
            for (String listId : listIdArray) {
            	queryVO = new BlockedRecordVO();
            	queryVO.setQueryBlockType(BlockType.MAC.toString());
            	queryVO.setQueryListId(listId);
                
                recordList = blockedRecordService.findModuleBlockedList(queryVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    brVO = recordList.get(0);
                    if(StringUtils.isBlank(brVO.getUndoScriptCode())) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    // Step 2. 查解鎖腳本
                    boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, brVO.getUndoScriptCode(), isAdmin);
                    if(retVO == null) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    pVO.setScriptCode(brVO.getUndoScriptCode());
                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
        			if(StringUtils.isNotBlank(retVO.getCheckScriptVariable())) {
        				gson = new Gson();
                        varKeys.addAll(gson.fromJson(retVO.getCheckScriptVariable(), new TypeToken<List<String>>(){}.getType()));
        			}
        			
                    groupId = brVO.getGroupId();
                    deviceId = brVO.getDeviceId();

                    varValue = new ArrayList<>();
                    for (String key : varKeys) {
						if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getPort()); // PORT_ID
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getMacAddress()); // MAC_ADDRESS
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getGlobalValue()); // GLOBAL_VALUE
						}
					}
                    
                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }else {
                	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無鎖定紀錄無法解除鎖定");
                }
            }

            // Step 3. 呼叫共用
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
        } finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
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

        DeliveryServiceVO retVO = null;
        DeliveryParameterVO pVO;
        try {
        	pVO = new DeliveryParameterVO();
            List<String> groupIds = new ArrayList<>();
            List<String> deviceIds = new ArrayList<>();
            List<String> varKeys = new ArrayList<>();
            List<List<String>> varValues = new ArrayList<>();

            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            
            // Step 1. 準備必要參數
            BlockedRecordVO queryVO, brVO;
            List<BlockedRecordVO> recordList = null;
            for (String listId : listIdArray) {
            	queryVO = new BlockedRecordVO();
            	queryVO.setQueryBlockType(BlockType.PORT.toString());
            	queryVO.setQueryListId(listId);
                
                recordList = blockedRecordService.findModuleBlockedList(queryVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    brVO = recordList.get(0);
                    if(StringUtils.isBlank(brVO.getUndoScriptCode())) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    // Step 2. 查解鎖腳本
                    boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, brVO.getUndoScriptCode(), isAdmin);
                    if(retVO == null) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    pVO.setScriptCode(brVO.getUndoScriptCode());
                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
        			if(StringUtils.isNotBlank(retVO.getCheckScriptVariable())) {
        				gson = new Gson();
                        varKeys.addAll(gson.fromJson(retVO.getCheckScriptVariable(), new TypeToken<List<String>>(){}.getType()));
        			}
        			
                    groupId = brVO.getGroupId();
                    deviceId = brVO.getDeviceId();

                    varValue = new ArrayList<>();
                    for (String key : varKeys) {
						if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getPort()); // PORT_ID

						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getGlobalValue()); // GLOBAL_VALUE
						}
					}
                    
                    groupIds.add(groupId);
                    deviceIds.add(deviceId);
                    varValues.add(varValue);
                }else {
                	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無鎖定紀錄無法解除鎖定");
                }
            }

            // Step 3. 呼叫共用
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
        } finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
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

            String groupId = null;
            String deviceId = null;
            String varKeyJson = null;
            List<String> varValue = null;
            String reason = reasonInput;
            
            // Step 1. 準備必要參數
            BlockedRecordVO queryVO, brVO;
            List<BlockedRecordVO> recordList = null;
            for (String listId : listIdArray) {
            	queryVO = new BlockedRecordVO();
            	queryVO.setQueryBlockType(BlockType.IP.toString());
            	queryVO.setQueryListId(listId);
                
                recordList = blockedRecordService.findModuleBlockedList(queryVO, null, null);

                if (recordList != null && !recordList.isEmpty()) {
                    brVO = recordList.get(0);
                    if(StringUtils.isBlank(brVO.getUndoScriptCode())) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    // Step 2. 查解鎖腳本
                    boolean isAdmin = (boolean)request.getSession().getAttribute(Constants.ISADMIN);
                    retVO = deliveryService.getScriptInfoByIdOrCode(null, brVO.getUndoScriptCode(), isAdmin);
                    
                    if(retVO == null) {
                    	return new AppResponse(HttpServletResponse.SC_BAD_REQUEST, "查無開通腳本無法解鎖!");
                    }
                    
                    pVO.setScriptCode(brVO.getUndoScriptCode());
                    varKeyJson = retVO.getActionScriptVariable();
                    Gson gson = new Gson();
                    varKeys = gson.fromJson(varKeyJson, new TypeToken<List<String>>(){}.getType());
        			if(StringUtils.isNotBlank(retVO.getCheckScriptVariable())) {
        				gson = new Gson();
                        varKeys.addAll(gson.fromJson(retVO.getCheckScriptVariable(), new TypeToken<List<String>>(){}.getType()));
        			}
        			
                    groupId = brVO.getGroupId();
                    deviceId = brVO.getDeviceId();

                    varValue = new ArrayList<>();
                    for (String key : varKeys) {
						if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getPort()); // PORT_ID
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getIpAddress()); // IP_ADDRESS
							
						} else if(StringUtils.equalsIgnoreCase(Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""), key)) {
							varValue.add(brVO.getGlobalValue()); // GLOBAL_VALUE
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
        } finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
		}
    }

	/**
	 * 查找被封鎖過的紀錄
	 * @param model
	 * @param request
	 * @param response
	 * @param queryGroupId
	 * @param queryDeviceId
	 * @param queryStatusFlag
	 * @param onlyOneScript
	 * @param startNum
	 * @param pageLength
	 * @param searchValue
	 * @param orderColIdx
	 * @param orderDirection
	 * @return
	 */
	@RequestMapping(value = "getBlockedData.json", method = RequestMethod.POST)
    public @ResponseBody DatatableResponse getBlockedData(
            Model model, HttpServletRequest request, HttpServletResponse response,
            @RequestParam(name="queryGroupId", required=false, defaultValue="") String queryGroupId,
            @RequestParam(name="queryDeviceId", required=false, defaultValue="") String queryDeviceId,
            @RequestParam(name="queryStatusFlag", required=false, defaultValue="") String queryStatusFlag,
            @RequestParam(name="onlyOneScript", required=false, defaultValue="") String onlyOneScript,
            @RequestParam(name="start", required=false, defaultValue="0") Integer startNum,
            @RequestParam(name="length", required=false, defaultValue="10") Integer pageLength,
            @RequestParam(name="search[value]", required=false, defaultValue="") String searchValue,
            @RequestParam(name="order[0][column]", required=false, defaultValue="5") Integer orderColIdx,
            @RequestParam(name="order[0][dir]", required=false, defaultValue="desc") String orderDirection) {

        long total = 0;
        long filterdTotal = 0;
        List<BlockedRecordVO> dataList = new ArrayList<>();
        BlockedRecordVO brVO;
        try {
        	
            brVO = new BlockedRecordVO();

            setQueryGroupList(request, brVO, StringUtils.isNotBlank(queryGroupId) ? "queryGroupId" : "queryGroupIdList", queryGroupId);
            setQueryDeviceList(request, brVO, StringUtils.isNotBlank(queryDeviceId) ? "queryDeviceId" : "queryDeviceIdList", queryGroupId, queryDeviceId);

            if (StringUtils.isNotBlank(queryStatusFlag)) {
            	brVO.setQueryStatusFlag(Arrays.asList(queryStatusFlag));
            }
            
            switch (onlyOneScript) {
				case Constants.DELIVERY_ONLY_SCRIPT_OF_SWITCH_PORT:
					brVO.setOrderColumn(UI_BLOCKED_PORT_RECORD_COLUMNS[orderColIdx]);
					brVO.setQueryBlockType(BlockType.PORT.toString());
					break;
	
				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_OPEN_BLOCK:
					brVO.setOrderColumn(UI_BLOCKED_IP_RECORD_COLUMNS[orderColIdx]);
					brVO.setQueryBlockType(BlockType.IP.toString());
					break;
	
				case Constants.DELIVERY_ONLY_SCRIPT_OF_MAC_OPEN_BLOCK:
					brVO.setOrderColumn(UI_BLOCKED_MAC_RECORD_COLUMNS[orderColIdx]);
					brVO.setQueryBlockType(BlockType.MAC.toString());
					break;
					
				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_MAC_BINDING:
					brVO.setOrderColumn(UI_IP_MAC_BOUND_RECORD_COLUMNS[orderColIdx]);
					brVO.setQueryBlockType(BlockType.BIND.toString());
					break;
				
				default:
					break;
			}
            
            brVO.setSearchValue(searchValue);
            brVO.setOrderDirection(orderDirection);

            boolean isAdmin = (boolean) request.getSession().getAttribute(Constants.ISADMIN);
			brVO.setAdmin(isAdmin);
			
            if(showSyncAction && Constants.STATUS_FLAG_SYNC.equals(queryStatusFlag) && StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            	
				String prtgLoginAccount = Objects.toString(request.getSession().getAttribute(Constants.PRTG_LOGIN_ACCOUNT), "");

				if (BlockType.IP.toString().equals(brVO.getQueryBlockType())) {
					blockedRecordService.doSyncDeviceIpBlockedList(isAdmin, prtgLoginAccount, brVO, dataList);
				}
				if (BlockType.PORT.toString().equals(brVO.getQueryBlockType())) {
					blockedRecordService.doSyncDevicePortBlockedList(isAdmin, prtgLoginAccount, brVO, dataList);
				}
				if (BlockType.MAC.toString().equals(brVO.getQueryBlockType())) {
					blockedRecordService.doSyncDeviceMacBlockedList(isAdmin, prtgLoginAccount, brVO, dataList);
				}
				
        	}

            filterdTotal = blockedRecordService.countModuleBlockedList(brVO);

            if (filterdTotal != 0) {
                dataList = blockedRecordService.findModuleBlockedList(brVO, startNum, pageLength);
            }
            
            total = filterdTotal;   //不顯示所有筆數，只顯示符合條件的筆數

        } catch (ServiceLayerException sle) {
        } catch (Exception e) {

        } finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
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
			dsVO.setQueryScriptTypeCode(Arrays.asList(queryScriptTypeCode));
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
					dsVO.setQueryScriptTypeCode(Arrays.asList(ScriptType.PORT_.toString()));
					break;

				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_OPEN_BLOCK:
					dsVO.setQueryScriptTypeCode(Arrays.asList(ScriptType.IP_.toString(), ScriptType.IP_CTR_.toString()));
					break;

				case Constants.DELIVERY_ONLY_SCRIPT_OF_MAC_OPEN_BLOCK:
					dsVO.setQueryScriptTypeCode(Arrays.asList(ScriptType.MAC_.toString()));
					break;
					
				case Constants.DELIVERY_ONLY_SCRIPT_OF_IP_MAC_BINDING:
					dsVO.setQueryScriptTypeCode(Arrays.asList(ScriptType.BIND_.toString()));
					break;					
			}

			filterdTotal = deliveryService.countScriptList(dsVO);

			if (filterdTotal != 0) {

				List<String> groupIds = new ArrayList<>();
				groupListMap.forEach((k, v) -> groupIds.add(k));
				
				dataList = deliveryService.findScriptList(dsVO, groupIds, startNum, pageLength);
			}

			total = deliveryService.countScriptList(new DeliveryServiceVO());

		} catch (ServiceLayerException sle) {
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			behaviorLog(request.getRequestURI(), request.getQueryString());
		}

		return new DatatableResponse(total, dataList, filterdTotal);
	}
}
