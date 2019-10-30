package com.cmap.plugin.module.port.status.viewer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.DeviceDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.DeviceLoginInfo;
import com.cmap.model.MibOidMapping;
import com.cmap.service.MibService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.MibVO;
import com.cmap.utils.ConnectUtils;
import com.cmap.utils.impl.SnmpV2Utils;
import com.mysql.cj.util.StringUtils;

@Service("portStatusViewerService")
public class PortStatusViewerServiceImpl extends CommonServiceImpl implements PortStatusViewerService {
	@Log
    private static Logger log;
	
	@Autowired
    private DeviceDAO deviceDAO;
	
	@Autowired
    private MibService mibService;
	
	@Override
	public List<PortStatusViewerVO> getPortStatusList(PortStatusViewerVO psvVO) throws ServiceLayerException {
		List<PortStatusViewerVO> retList = new ArrayList<>();
		
		ConnectUtils snmpUtils = null;
		try {
			final String queryGroupId = psvVO.getQueryGroupId();
			final String queryDeviceId = psvVO.getQueryDeviceId();
			
			// Step 1. 查找設備基本資料
			DeviceList device = deviceDAO.findDeviceListByGroupAndDeviceId(queryGroupId, queryDeviceId);
			
			if (device == null) {
				throw new ServiceLayerException("查無設備資料，請重新操作");
			}
			
			// Step 2. 準備IF_TABLE的OID清單
        	List<MibOidMapping> ifTableOidMapping = mibService.findMibOidMappingByNames(Arrays.asList(new String[] {Env.OID_NAME_OF_IF_TABLE}));

        	if (ifTableOidMapping == null || (ifTableOidMapping != null && ifTableOidMapping.isEmpty())) {
        		throw new ServiceLayerException("未設定 IF_Table OID!! >> [OID_NAME: ifTable]");
        	}

        	String ifTableOid = ifTableOidMapping.get(0).getOidValue();

        	// Step 3. 準備IF_TABLE底下需要的Field的OID清單
        	Map<String, String> tableEntryMap = null;
        	List<MibOidMapping> ifTableEntryOidMapping = mibService.findMibOidMappingOfTableEntryByNameLike(Env.OID_NAME_OF_IF_TABLE);
        	if (ifTableEntryOidMapping != null && !ifTableEntryOidMapping.isEmpty()) {
        		tableEntryMap = new HashMap<>();
        		for (MibOidMapping mapping : ifTableEntryOidMapping) {
        			tableEntryMap.put(mapping.getOidName(), mapping.getOidValue());
        		}
        	}
        	
        	List<PortStatusViewerVO> preRetList = new ArrayList<>();
        	// Step 4. 連線設備取得IF_TABLE資料
        	try {
        		snmpUtils = new SnmpV2Utils();

            	String deviceListId = device.getDeviceListId();
                String groupId = device.getGroupId();
                String groupName = device.getGroupName();
                String deviceId = device.getDeviceId();
                String deviceName = device.getDeviceName();
                String deviceIp = device.getDeviceIp();

                // 取得設備 COMMUNITY_STRING 及 UDP_PORT 設定
                String communityString = null;
                Integer udpPort = null;
                DeviceLoginInfo loginInfo = findDeviceLoginInfo(deviceListId, groupId, deviceId);
                if (loginInfo != null) {
                	communityString = loginInfo.getCommunityString();
                	udpPort = loginInfo.getUdpPort();
                } else {
                	communityString = Env.DEFAULT_DEVICE_COMMUNITY_STRING;
                	udpPort = Env.DEFAULT_DEVICE_UDP_PORT;
                }

                String udpAddress = "udp:" + deviceIp + "/" + udpPort;
                // 連接設備
                snmpUtils.connect(udpAddress, communityString);

                // 撈取設備IF_TABLE相關資料
                Map<String, Map<String, String>> ifTable = snmpUtils.pollTableView(ifTableOid, tableEntryMap);

                if (ifTable == null || (ifTable != null && ifTable.isEmpty())) {
                	throw new ServiceLayerException("查無該設備IF_TABLE資料，請重新操作");
                }
                
                // 查找 IF_TABLE 中 Entry value 說明表
                Map<String, Map<String, MibVO>> entryValueMapping = mibService.findMibValueMappingByOidTable(Env.OID_NAME_OF_IF_TABLE);

                // 將撈取結果組成此Method回傳MAP格式
                PortStatusViewerVO vo = null;
                for (Map.Entry<String, Map<String, String>> ifTableMap : ifTable.entrySet()) {
                	vo = new PortStatusViewerVO();

                	Map<String, String> ifTableEntryMap = ifTableMap.getValue();
                	String portIndex = ifTableEntryMap.get(Env.OID_NAME_OF_IF_TABLE_INDEX);
                	String portName = ifTableEntryMap.get(Env.OID_NAME_OF_IF_TABLE_DESCRIPTION);
                	String portAdminStatus = ifTableEntryMap.get(Env.OID_NAME_OF_IF_TABLE_ADMIN_STATUS);
                	String portOperStatus = ifTableEntryMap.get(Env.OID_NAME_OF_IF_TABLE_OPER_STATUS);
                	
                	// 判斷此 PortName 是否在要排除的清單內
                	List<String> excludePortNameList = Env.PORT_STATUS_EXCLUDE_IF_NAME_LIKE;
                	if (excludePortNameList != null && !excludePortNameList.isEmpty()) {
                		boolean keepGoing = true; // false=要排除此筆Port
                		for (String exPortName : excludePortNameList) {
                			if (StringUtils.indexOfIgnoreCase(portName, exPortName) != -1) {
                				// 若在排除清單內則跳過此筆資料
                				keepGoing = false;
                				break;
                			}
                		}
                		
                		if (!keepGoing) {
                			continue;
                		}
                	}
                	
                	vo.setGroupId(groupId);
                	vo.setGroupName(groupName);
                	vo.setDeviceId(deviceId);
                	vo.setDeviceName(deviceName);
                	vo.setPortIndex(portIndex);
                	vo.setPortName(portName);
                	vo.setPortAdminStatus(portAdminStatus);
                	vo.setPortOperStatus(portOperStatus);
                	
                	/*
                	 * 判斷 & 塞入狀態說明及UI要呈顯的燈號
                	 */
                	MibVO adminStatusVO = null;
                	MibVO operStatusVO = null;
                	if (entryValueMapping != null && !entryValueMapping.isEmpty()) {
                		Map<String, MibVO> adminStatusMapping = entryValueMapping.get(Env.OID_NAME_OF_IF_TABLE_ADMIN_STATUS);
                		Map<String, MibVO> operStatusMapping = entryValueMapping.get(Env.OID_NAME_OF_IF_TABLE_OPER_STATUS);
                		
                		if (adminStatusMapping != null && adminStatusMapping.containsKey(portAdminStatus)) {
                			adminStatusVO = adminStatusMapping.get(portAdminStatus);
                			
                			if (adminStatusVO != null) {
                    			vo.setPortAdminStatusDesc(adminStatusVO.getEntryValueDesc());
                    			vo.setPortAdminStatusPresentType(adminStatusVO.getUiPresentType());
                    		}
                		}
                		if (operStatusMapping != null && operStatusMapping.containsKey(portOperStatus)) {
                			operStatusVO = operStatusMapping.get(portOperStatus);
                			
                			if (operStatusVO != null) {
                    			vo.setPortOperStatusDesc(operStatusVO.getEntryValueDesc());
                    			vo.setPortOperStatusPresentType(operStatusVO.getUiPresentType());
                    		}
                		}
                	}

                	preRetList.add(vo);
                }

        	} catch (Exception e) {
        		throw e;

        	} finally {
                if (snmpUtils != null) {
                    try {
                        snmpUtils.disconnect();

                    } catch (Exception e) {
                        snmpUtils = null;
                    }
                }
            }
        	
        	// Step 5. 處理Order By，預設以 Port_Name 排序
        	if (preRetList != null && !preRetList.isEmpty()) {
        		Map<String, PortStatusViewerVO> orderMap = new TreeMap<>();
        		
        		for (PortStatusViewerVO vo : preRetList) {
        			String pName = vo.getPortName();
        			orderMap.put(pName, vo);
        		}
        		
        		for (PortStatusViewerVO fVO : orderMap.values()) {
        			retList.add(fVO);
        		}
        	}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			throw new ServiceLayerException("查詢異常，請重新操作");
		}
		return retList;
	}
}
