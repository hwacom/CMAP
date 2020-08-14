package com.cmap.plugin.module.blocked.record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.comm.enums.BlockType;
import com.cmap.comm.enums.ScriptType;
import com.cmap.dao.BaseDAO;
import com.cmap.dao.ScriptInfoDAO;
import com.cmap.dao.vo.ScriptInfoDAOVO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.model.DeviceList;
import com.cmap.model.ScriptInfo;
import com.cmap.service.DeliveryService;
import com.cmap.service.ScriptService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.DeliveryParameterVO;
import com.cmap.service.vo.DeliveryServiceVO;

@Service("blockedRecordService")
@Transactional
public class BlockedRecordServiceImpl extends CommonServiceImpl implements BlockedRecordService {
    @Log
    private static Logger log;

    @Autowired
    private BlockedRecordDAO blockedRecordDAO;

    @Autowired
	private ScriptService scriptService;
    
    @Autowired
	private ScriptInfoDAO scriptInfoDAO;
    
    @Autowired
	private DeliveryService deliveryService;
    
    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleBlockedList(BlockedRecordVO ibrVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = blockedRecordDAO.countModuleBlockedList(ibrVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<BlockedRecordVO> findModuleBlockedList(BlockedRecordVO ibrVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<BlockedRecordVO> retList = new ArrayList<>();
        try {
            String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
            String msgOpen = messageSource.getMessage("status.flag.open", Locale.TAIWAN, null);         // O-開通
            String msgUnknown = messageSource.getMessage("status.flag.unknown", Locale.TAIWAN, null);   // U-未知

            List<Object[]> entities = blockedRecordDAO.findModuleBlockedList(ibrVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                return retList;
            }

            BlockedRecordVO vo;
            for (Object[] entity : entities) {
                String groupId = Objects.toString(entity[0]);
                String groupName = Objects.toString(entity[1]);
                String deviceId = Objects.toString(entity[2]);
                String blockType = Objects.toString(entity[3]);
                String ipAddress = Objects.toString(entity[4], null);
                String ipDesc = Objects.toString(entity[5], Env.IP_DESC_NULL_SHOW_WHAT);
                String macAddress = Objects.toString(entity[6], null);
                String port = Objects.toString(entity[7], null);
                String globalValue = Objects.toString(entity[8], null);
                String statusFlag = Objects.toString(entity[9]);
                String scriptCode = Objects.toString(entity[10]);
                String scriptName = Objects.toString(entity[11], null);
                String undoScriptCode = Objects.toString(entity[12]);
                Timestamp blockTime = entity[13] != null ? (Timestamp)entity[13] : null;
                String blockBy = Objects.toString(entity[14], null);
                String blockReason = Objects.toString(entity[15], null);
                Timestamp openTime = entity[16] != null ? (Timestamp)entity[16] : null;
                String openBy = Objects.toString(entity[17], null);
                String openReason = Objects.toString(entity[18], null);
                Timestamp updateTime = entity[19] != null ? (Timestamp)entity[19] : null;
                String updateBy = Objects.toString(entity[20], null);
                String listId = Objects.toString(entity[21]);
                
                vo = new BlockedRecordVO();
                vo.setListId(listId);
                vo.setGroupId(groupId);
                vo.setGroupName(groupName);
                vo.setDeviceId(deviceId);
                vo.setBlockType(blockType);
                vo.setIpAddress(ipAddress);
                vo.setIpDesc(ipDesc);
                vo.setMacAddress(macAddress);
                vo.setPort(port);
                vo.setGlobalValue(globalValue);
                vo.setStatusFlag(StringUtils.equals(statusFlag, Constants.STATUS_FLAG_BLOCK)
                        ? msgBlock
                        : StringUtils.equals(statusFlag, Constants.STATUS_FLAG_OPEN)
                            ? msgOpen
                            : msgUnknown);
			    vo.setScriptCode(scriptCode);
			    vo.setScriptName(scriptName);
			    vo.setUndoScriptCode(undoScriptCode);
                vo.setBlockTimeStr(blockTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(blockTime) : null);
                vo.setBlockBy(blockBy);
                vo.setBlockReason(blockReason);
                vo.setOpenTimeStr(openTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(openTime) : null);
                vo.setOpenBy(openBy);
                vo.setOpenReason(openReason);
                vo.setUpdateTimeStr(updateTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(updateTime) : null);
                vo.setUpdateBy(updateBy);
                
                retList.add(vo);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retList;
    }

    @Override
    public List<ModuleBlockedList> findModuleBlockedList(BlockedRecordVO ibrVO)
            throws ServiceLayerException {
    	
    	return blockedRecordDAO.findModuleBlockedList(ibrVO);
    }
    private ModuleBlockedList transVO2Model(BlockedRecordVO ibrVO) {
        ModuleBlockedList entity = new ModuleBlockedList();
        BeanUtils.copyProperties(ibrVO, entity);
        entity.setCreateTime(currentTimestamp());
        entity.setCreateBy(currentUserName());
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    private BlockedRecordVO transModel2VO(ModuleBlockedList entity) {
    	BlockedRecordVO ibrVO = new BlockedRecordVO();
        BeanUtils.copyProperties(entity, ibrVO);
        return ibrVO;
    }
            
    @Override
    public void writeModuleBlockListRecord(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String remark) throws ServiceLayerException {
        
        // 定義IP封鎖腳本中「IP_Address」的變數名稱 for 寫入異動紀錄table使用
        String ipAddressVarKey = Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK;
        String macAddressVarKey = Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK;
        String portIdVarKey = Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK;
        String globalVarKey = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING;
        
        List<BlockedRecordVO> brVOs = new ArrayList<>();
        BlockedRecordVO brVO = null;
        
        for (Map<String, String> varMap : varMapList) {
            
            brVO = new BlockedRecordVO();
            brVO.setGroupId(ciVO.getGroupId());
            brVO.setDeviceId(ciVO.getDeviceId());
            if(varMap.containsKey(ipAddressVarKey)) {
            	brVO.setIpAddress(varMap.get(ipAddressVarKey));
            }
            if (varMap.containsKey(macAddressVarKey)) {
				brVO.setPort(varMap.get(macAddressVarKey));
			}
            if(varMap.containsKey(portIdVarKey)) {
            	brVO.setPort(varMap.get(portIdVarKey));
            }
            if(varMap.containsKey(globalVarKey)) {
            	brVO.setGlobalValue(varMap.get(globalVarKey));            	
            }
            
            
            brVO.setRemark(remark);
            brVO.setBlockType(scriptCode.substring(0, scriptCode.indexOf("_")));
            
            ScriptInfo info = scriptService.getScriptInfoEntityByScriptCode(scriptCode);
            brVO.setScriptCode(scriptCode);
            brVO.setScriptName(info.getScriptName());
        	brVO.setUndoScriptCode(info.getUndoScriptCode());
        	if(StringUtils.isNotBlank(info.getUndoScriptCode())) {
        		brVO.setBlockBy(currentUserName());
                brVO.setBlockReason(remark);
                brVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
        	}else {
        		brVO.setOpenBy(currentUserName());
                brVO.setOpenReason(remark);
                brVO.setStatusFlag(Constants.STATUS_FLAG_OPEN);
        	}
        	
            brVOs.add(brVO);
        }
        
        if (brVOs != null && !brVOs.isEmpty()) {
            saveOrUpdateRecord(brVOs);
        }
    }

    @Override
    public void saveOrUpdateRecord(List<BlockedRecordVO> ibrVOs) throws ServiceLayerException {
        try {
            BlockedRecordVO qVO;
            for (BlockedRecordVO ibrVO : ibrVOs) {
                qVO = new BlockedRecordVO();
                qVO.setQueryGroupId(ibrVO.getGroupId());                
                qVO.setQueryDeviceId(ibrVO.getDeviceId());
                qVO.setQueryBlockType(ibrVO.getBlockType());
                qVO.setQueryIpAddress(ibrVO.getIpAddress());
                qVO.setQueryMacAddress(ibrVO.getMacAddress());
                qVO.setQueryPortId(ibrVO.getPort());

                String actionStatusFlag = ibrVO.getStatusFlag();
                String preStatusFlag = "";

                ModuleBlockedList lastestRecord = blockedRecordDAO.findLastestModuleBlockedList(qVO);

                if (lastestRecord != null) {
                    preStatusFlag = lastestRecord.getStatusFlag();
                    
                    /*
                     * 有查到資料，依照情境處理:
                     * (1) B(前狀態) → B(目前執行) => 表示可能USER先前曾自己登入設備解鎖 or 重複執行 => 更新前一筆紀錄狀態為「U」& 再寫入一筆新的紀錄
                     * (2) B(前狀態) → O(目前執行) => 更新前一筆紀錄狀態為「O」
                     * (3) O(前狀態) → B(目前執行) => 寫入一筆新的紀錄
                     * (4) O(前狀態) → O(目前執行) => 表示可能USER先前是自己登入設備封鎖 or 根本沒封鎖過 => 寫入一筆新的紀錄
                     */
                    if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                    	                    	
                        if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                            // B → B
                            // Step 1. 更新前一筆紀錄狀態為「U」
                        	lastestRecord.setGlobalValue(ibrVO.getGlobalValue());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            
                            blockedRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                        } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                            // B → O
                            // 更新前一筆紀錄狀態為「O」
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_OPEN);
                            lastestRecord.setOpenTime(currentTimestamp());
                            lastestRecord.setOpenBy(currentUserName());
                            lastestRecord.setOpenReason(ibrVO.getOpenReason());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            blockedRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                        }

                    } else if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        // O → B 、 O → O
                        // 寫入一筆新的紀錄
                    	if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                    		ModuleBlockedList newRecord = transVO2Model(ibrVO);
                            newRecord.setBlockTime(currentTimestamp());
                            newRecord.setBlockBy(currentUserName());
                            blockedRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                            
                    	}else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                    		lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            blockedRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                    	}
                        
                    }

                } else {
                    /*
                     * 沒查到資料 => 寫入一筆新的紀錄
                     */
                    ModuleBlockedList newRecord = transVO2Model(ibrVO);

                    if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                        newRecord.setBlockTime(currentTimestamp());

                    } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        newRecord.setOpenTime(currentTimestamp());
                    }

                    blockedRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新封鎖紀錄時異常! (ModuleBlockedList)");
        }
    }
    

    @Override
    public BlockedRecordVO checkIpblockedList(String groupId, String deviceId, String ipAddress , List<BlockedRecordVO> dbRecordList) {
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
    	String sycReason = messageSource.getMessage("synchronize.switch.ip", Locale.TAIWAN, null);
    	
		for (BlockedRecordVO recVO : dbRecordList) {
			if (recVO.getGroupId().equals(groupId) && recVO.getDeviceId().equals(deviceId)
					&& recVO.getIpAddress().equals(ipAddress)
					&& (recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock))) {
				log.debug("IpBlockedRecord ==> 設備同步資訊比對相同，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + ipAddress + "，block_by," + recVO.getBlockBy());
				return recVO;
			}
			
		}
		
		BlockedRecordVO ibrVO = new BlockedRecordVO();
		ibrVO.setGroupId(groupId);
		ibrVO.setDeviceId(deviceId);
		ibrVO.setIpAddress(ipAddress);
		ibrVO.setStatusFlag(msgBlock);
		ibrVO.setBlockReason(sycReason);
		ibrVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
		ibrVO.setBlockType(BlockType.IP.toString());
		
		return ibrVO;
	}
    

    @Override
    public List<BlockedRecordVO> compareIpblockedList(List<BlockedRecordVO> dbRecordList, Map<String, BlockedRecordVO> compareMap) {
    	
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖    	
        List<BlockedRecordVO> resultList = new ArrayList<BlockedRecordVO>();
        
		for (BlockedRecordVO recVO : dbRecordList) {
			//不存在同步結果清單中的自動解鎖
			if(!compareMap.containsKey(recVO.getDeviceId()+recVO.getIpAddress())) {
				log.debug("IpBlockedRecord ==> 不存在同步結果清單中，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + recVO.getIpAddress() + "，block_by," + recVO.getBlockBy());
				if(recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock)) {
					recVO.setStatusFlag(Constants.STATUS_FLAG_OPEN);
					recVO.setOpenReason("Switch內查無封鎖記錄");
					recVO.setOpenBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
					resultList.add(recVO);
				}
			}
		}
		
		return resultList;
	}
    

    @Override
    public BlockedRecordVO checkPortBlockedList(String groupId, String deviceId, String portId , List<BlockedRecordVO> dbRecordList) {
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
    	String sycReason = messageSource.getMessage("synchronize.switch.port", Locale.TAIWAN, null);
    	
		for (BlockedRecordVO recVO : dbRecordList) {
			if (recVO.getGroupId().equals(groupId) && recVO.getDeviceId().equals(deviceId)
					&& recVO.getPort().equals(portId)
					&& (recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock))) {
				log.debug("IpBlockedRecord ==> 設備同步資訊比對相同，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + portId + "，block_by," + recVO.getBlockBy());
				return recVO;
			}
			
		}
		
		BlockedRecordVO brVO = new BlockedRecordVO();
		brVO.setGroupId(groupId);
		brVO.setDeviceId(deviceId);
		brVO.setPort(portId);
		brVO.setStatusFlag(msgBlock);
		brVO.setBlockReason(sycReason);
		brVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
		brVO.setBlockType(BlockType.PORT.toString());
		
		return brVO;
	}
    
    @Override
    public List<BlockedRecordVO> comparePortBlockedList(List<BlockedRecordVO> dbRecordList, Map<String, BlockedRecordVO> compareMap) {
    	
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
        List<BlockedRecordVO> resultList = new ArrayList<BlockedRecordVO>();
        
		for (BlockedRecordVO recVO : dbRecordList) {
			//不存在同步結果清單中的自動解鎖
			if(!compareMap.containsKey(recVO.getDeviceId()+recVO.getPort())) {
				log.debug("comparePortblockedList ==> 不存在同步結果清單中，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + recVO.getPort() + "，block_by," + recVO.getBlockBy());
				if(recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock)) {
					recVO.setStatusFlag(Constants.STATUS_FLAG_OPEN);
					recVO.setOpenReason("Switch內查無封鎖記錄");
					recVO.setOpenBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
					resultList.add(recVO);
				}
			}
			
		}
		
		return resultList;
	}
    

    @Override
    public BlockedRecordVO checkMacBlockedList(String groupId, String deviceId, String macAddress , List<BlockedRecordVO> dbRecordList) {
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
    	String sycReason = messageSource.getMessage("synchronize.switch.mac", Locale.TAIWAN, null);
    	
		for (BlockedRecordVO recVO : dbRecordList) {
			if (recVO.getGroupId().equals(groupId) && recVO.getDeviceId().equals(deviceId)
					&& recVO.getMacAddress().equals(macAddress)
					&& (recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock))) {
				log.debug("IpBlockedRecord ==> 設備同步資訊比對相同，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + macAddress + "，block_by," + recVO.getBlockBy());
				return recVO;
			}
			
		}
		
		BlockedRecordVO brVO = new BlockedRecordVO();
		brVO.setGroupId(groupId);
		brVO.setDeviceId(deviceId);
		brVO.setMacAddress(macAddress);
		brVO.setStatusFlag(msgBlock);
		brVO.setBlockReason(sycReason);
		brVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
		brVO.setBlockType(BlockType.MAC.toString());
		
		return brVO;
	}
    
    @Override
    public List<BlockedRecordVO> compareMacBlockedList(List<BlockedRecordVO> dbRecordList, Map<String, BlockedRecordVO> compareMap) {
    	
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
        List<BlockedRecordVO> resultList = new ArrayList<BlockedRecordVO>();
        
		for (BlockedRecordVO recVO : dbRecordList) {
			//不存在同步結果清單中的自動解鎖
			if(!compareMap.containsKey(recVO.getDeviceId()+recVO.getMacAddress())) {
				log.debug("compareMacBlockedList ==> 不存在同步結果清單中，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + recVO.getMacAddress()+ "，block_by," + recVO.getBlockBy());
				if(recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock)) {
					recVO.setStatusFlag(Constants.STATUS_FLAG_OPEN);
					recVO.setOpenReason("Switch內查無封鎖記錄");
					recVO.setOpenBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
					resultList.add(recVO);
				}
			}
			
		}
		
		return resultList;
	}
    

	@Override
    public  boolean doSyncDeviceIpBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE);
		}
		if (isAdmin) {
			//deviceList = findGroupDeviceOfSpecifyLayer(null, Env.DEVICE_LAYER_L3);

			// 若使用者為管理者，多查出中心端的IP控制腳本
			if (Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE_4_ADMIN != null) {
				scriptList.addAll(Env.DELIVERY_SYNC_IP_BLOCK_RECORD_SCRIPT_CODE_4_ADMIN);
			}
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch IP封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String ipAddress = null;
		String blockCmd = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						ipAddress = null;
						blockCmd = null;
						
						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						//判斷acl封鎖
						while(provisionLog.contains("deny ip host")) {
							provisionLog = provisionLog.substring(provisionLog.indexOf("deny ip host"));
							ipAddress = provisionLog.substring(provisionLog.indexOf("deny ip host")+12, provisionLog.indexOf(" any")).trim();
							blockCmd = provisionLog.substring(provisionLog.indexOf("deny ip host"), provisionLog.indexOf(" any")+4).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf(" any")+4).trim();
							
							nowbrVO = checkIpblockedList(device.getGroupId(), device.getDeviceId(), ipAddress , dbRecordList);
							
							
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("acl封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ACL", device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
								updateList.add(nowbrVO);							
								
							}else {
								if(StringUtils.isBlank(nowbrVO.getScriptCode())) {
									info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ACL", device.getDeviceModel());
									if(info != null) {
										nowbrVO.setScriptCode(info.getScriptCode());
										nowbrVO.setScriptName(info.getScriptName());
										
										updateList.add(nowbrVO);
									}
								}
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
							}
						}
						
						//判斷arp封鎖
						while(provisionLog.contains("0000.0000.0001")) {
							provisionLog = provisionLog.substring(provisionLog.indexOf("Internet"));
							ipAddress = provisionLog.substring(provisionLog.indexOf("Internet")+8, provisionLog.indexOf(" 0000.0000.0001")).replaceAll("-", "").trim();
							blockCmd = provisionLog.substring(provisionLog.indexOf("Internet"), provisionLog.indexOf(" ARPA")+5);
							provisionLog = provisionLog.substring(provisionLog.indexOf(" ARPA")+5).trim();

							nowbrVO = checkIpblockedList(device.getGroupId(), device.getDeviceId(), ipAddress,dbRecordList);
							
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("arp封鎖同步");
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ARP", device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
								updateList.add(nowbrVO);
							}else {				
								if(StringUtils.isBlank(nowbrVO.getScriptCode())) {
									info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.IP_BLOCK+"_ARP", device.getDeviceModel());
									if(info != null) {
										nowbrVO.setScriptCode(info.getScriptCode());
										nowbrVO.setScriptName(info.getScriptName());
										
										updateList.add(nowbrVO);
									}
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getIpAddress(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(compareIpblockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}


	@Override
    public  boolean doSyncDevicePortBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_SWITCH_PORT_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_SWITCH_PORT_RECORD_SCRIPT_CODE);
			//show interfaces status disabled
		}
		if (isAdmin) {
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch Port封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String portId = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
//					dpVO.setGroupId(Arrays.asList(device.getGroupId()));
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						portId = null;

						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						while(provisionLog.contains("disabled")) {
							portId = provisionLog.substring(0, provisionLog.indexOf(" disabled")).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf("1000BaseTX")+10).trim();

							nowbrVO = checkPortBlockedList(device.getGroupId(), device.getDeviceId(), portId,dbRecordList);
														
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("Port封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.PORT_BLOCK.toString(), device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getPort(), nowbrVO);
								updateList.add(nowbrVO);
							}else {								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getPort(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(comparePortBlockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}
	

	@Override
    public  boolean doSyncDeviceMacBlockedList(boolean isAdmin, String prtgLoginAccount, BlockedRecordVO brVO,
			List<BlockedRecordVO> dbRecordList) throws ServiceLayerException {
		
		List<DeviceList> deviceList = new ArrayList<DeviceList>();
		List<String> scriptList = new ArrayList<>();
		List<String> searchLayer = new ArrayList<String>();
		searchLayer.add(Env.DEVICE_LAYER_L3);
		
		if (Env.DELIVERY_SYNC_MAC_BLOCK_RECORD_SCRIPT_CODE != null) {
			scriptList.addAll(Env.DELIVERY_SYNC_MAC_BLOCK_RECORD_SCRIPT_CODE);
			//show interfaces status disabled
		}
		if (isAdmin) {
			// 若使用者為管理者，多查出LC Layer Device
			searchLayer.add(Env.DEVICE_LAYER_LC);
		}
		
		if (StringUtils.isNotBlank(brVO.getQueryGroupId())) {
            deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(brVO.getQueryGroupId(),searchLayer));
        } else if (brVO.getQueryGroupIdList() != null && !brVO.getQueryGroupIdList().isEmpty()) {
        	for (String groupId : brVO.getQueryGroupIdList()) {
        		deviceList.addAll(deliveryService.findGroupDeviceOfSpecifyLayer(groupId, searchLayer));
			}
        }
		
		Map<String, BlockedRecordVO> result = new HashMap<String, BlockedRecordVO>();
		String reason = prtgLoginAccount + "點選同步Switch Mac封鎖記錄 按鈕";
		
		DeliveryServiceVO dsVO;
		DeliveryParameterVO dpVO;
		List<BlockedRecordVO> updateList = new ArrayList<>();
		BlockedRecordVO nowbrVO = null;
		String macAddress = null;
		ScriptInfo scriptInfo ;
		ScriptInfo info ;
		
		for (String scriptCode : scriptList) {
			for (DeviceList device : deviceList) {

				try {
					dpVO = new DeliveryParameterVO();
//					dpVO.setGroupId(Arrays.asList(device.getGroupId()));
					dpVO.setDeviceId(Arrays.asList(device.getDeviceId()));
					dpVO.setScriptCode(scriptCode);
					dpVO.setVarKey(null);
					dpVO.setVarValue(null);
					dpVO.setReason(reason);

					dsVO = deliveryService.doDelivery(Env.CONNECTION_MODE_OF_DELIVERY, dpVO, true, prtgLoginAccount, reason, false);
					if (dsVO.getProvisionLog() != null) {
						String provisionLog = dsVO.getProvisionLog();						
						macAddress = null;

						scriptInfo = scriptInfoDAO.findScriptInfoByIdOrCode(null, scriptCode);
						if(provisionLog.contains(scriptInfo.getActionScript())) {
							provisionLog = provisionLog.substring(provisionLog.indexOf(scriptInfo.getActionScript())+scriptInfo.getActionScript().length());
						}
						
						while(provisionLog.contains("Drop")) {
							//2    3415.9ece.d9bb    STATIC      Drop
							macAddress = provisionLog.substring(provisionLog.indexOf(".")-4, provisionLog.indexOf("STATIC")).trim();
							provisionLog = provisionLog.substring(provisionLog.indexOf("Drop")+4).trim();

							nowbrVO = checkMacBlockedList(device.getGroupId(), device.getDeviceId(), macAddress,dbRecordList);
														
							//不存在的新增一筆記錄
							if(StringUtils.isBlank(nowbrVO.getBlockBy())) {
								nowbrVO.setBlockBy(Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME != null ? Env.DELIVERY_SYNC_SWITCH_RECORD_ACTION_NAME : "SYSADMIN");
								nowbrVO.setRemark("Mac封鎖同步");
								
								info = scriptInfoDAO.findDefaultScriptInfoByScriptTypeAndSystemVersion(ScriptType.MAC_BLOCK.toString(), device.getDeviceModel());
								if(info != null) {
									nowbrVO.setScriptCode(info.getScriptCode());
									nowbrVO.setScriptName(info.getScriptName());
								}
								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getMacAddress(), nowbrVO);
								updateList.add(nowbrVO);
							}else {								
								result.put(nowbrVO.getDeviceId()+nowbrVO.getMacAddress(), nowbrVO);
							}
						}
					}

				} catch (Exception e) {
					log.error(e.toString(), e);

					// 設備執行腳本失敗則跳過此設備
					break;
				}

			}
		}
		
		updateList.addAll(compareMacBlockedList(dbRecordList, result));
		
		if (updateList != null && !updateList.isEmpty()) {
			saveOrUpdateRecord(updateList);
			return true;
        }
		return false;
	}
	

	@Override
    public  DeliveryParameterVO checkB4DoBindingDelivery(DeliveryParameterVO pVO) throws ServiceLayerException {
		//檢核IP MAC 綁定相關，放入global參數
		ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
		siDAOVO.setQueryScriptCode(pVO.getScriptCode());
		ScriptInfo dbEntity = scriptInfoDAO.findScriptInfoByIdOrCode(null, pVO.getScriptCode());
		if(dbEntity == null) {
			log.error("check before provision - script info not exist!!");
			throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
		}
		
		if(dbEntity.getScriptCode().startsWith(ScriptType.BIND_.toString())) {
			List<String> varKey = pVO.getVarKey();
			List<List<String>> varValue = pVO.getVarValue();
			List<List<String>> newVarValue = new ArrayList<List<String>>();
			List<String> deviceIdList = pVO.getDeviceId();
			
			// Step 3.檢核JSON內VarKey與系統內設定的腳本變數欄位是否相符
			final String dbVarKeyJSON = dbEntity.getCheckScriptVariable();
			final List<String> dbVarKeyList = (List<String>) transJSON2Object(dbVarKeyJSON, ArrayList.class);
			String keyGlobal = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			String keyNoFlag = Env.KEY_VAL_OF_NO_FLAG_WITH_CMD.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			
			if(dbVarKeyList != null && dbVarKeyList.size() > 0 && 
					StringUtils.isNotBlank(keyGlobal) 
					&& StringUtils.isNotBlank(keyNoFlag)) {
				
				int addKey = 0;
				if(dbVarKeyList.contains(keyGlobal)) {
					varKey.add(keyGlobal);
					addKey++;
				}
				if(dbVarKeyList.contains(keyNoFlag)) {
					varKey.add(keyNoFlag);	
					addKey++;
				}
				
				pVO.setVarKey(varKey);
				
				BlockedRecordVO brVO;
	            List<ModuleBlockedList> recordList = null;            	              
            	List<String> valueList = null;
            	Map<String, String>varMap = null;
            	Map<String, Integer>recordPortMap = null;
            	String compareValue = null, currentPort4Unbind = null;
            	String noFlagValue = "";
            	
				int deviceCount = deviceIdList == null ? 0: deviceIdList.size();
				//逐筆設備替換
				for (int idx = 0; idx < deviceCount; idx++) {
	                varMap = new HashMap<String, String>();
	                valueList = varValue.get(idx);
	                for(int i=0; i < varKey.size()-addKey; i++) {
	                	varMap.put(varKey.get(i), valueList.get(i));
                	}
	                
	                brVO = new BlockedRecordVO();  
	                brVO.setQueryDeviceId(deviceIdList.get(idx));
	                brVO.setQueryBlockType(BlockType.BIND.toString());
					brVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));	                
	                recordList = findModuleBlockedList(brVO);

	                recordPortMap = new HashMap<String, Integer>();
	                for(ModuleBlockedList bVO : recordList) {
	                	if(!recordPortMap.containsKey(bVO.getPort())) {
	                		recordPortMap.put(bVO.getPort(), 1);
	                	}else {
	                		recordPortMap.put(bVO.getPort(), recordPortMap.get(bVO.getPort())+1);
	                	}
	                	//如果比對IP、MAC與紀錄相同為解鎖作業
	                	if(StringUtils.equalsIgnoreCase(bVO.getIpAddress(), varMap.get(Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")))
	                			&& StringUtils.equalsIgnoreCase(bVO.getMacAddress(), varMap.get(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")))) {
	                		currentPort4Unbind = bVO.getPort();
	                	}
					}
	                
	                
	                if(varKey.contains(keyGlobal)) {
		                //綁定行為時，如果同設備中沒有其他封鎖紀錄使用相同port，global加入該port
		                if(StringUtils.isNotBlank(dbEntity.getUndoScriptCode())) {
		                	compareValue = varMap.get(Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, ""));
		                	if(recordPortMap.size() == 0) {
		                		valueList.add(compareValue);
		                		
		                	}else {		                		
		                		if(recordList.get(0).getGlobalValue().contains(compareValue)) {
		                			//有相同時，放進相同值
		                			valueList.add(recordList.get(0).getGlobalValue());
		                		}else {
		                			valueList.add(recordList.get(0).getGlobalValue()+","+compareValue); 
		                		}
		                	}
		                	noFlagValue = "";             	
		                }else {
		                	//解除綁定，如果同設備中沒有其他封鎖紀錄使用相同port，global取消該port		                	
	                		if(StringUtils.isEmpty(currentPort4Unbind)) {
	                			throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
	                		}
		                	if(!recordList.get(0).getGlobalValue().equalsIgnoreCase(currentPort4Unbind) && recordPortMap.get(currentPort4Unbind) == 1) {
		                		String globalValue = recordList.get(0).getGlobalValue().replaceAll(currentPort4Unbind+",", "");
	                			globalValue = globalValue.replaceAll(","+currentPort4Unbind+",", "");
	                			globalValue = globalValue.replaceAll(","+currentPort4Unbind, "");
	                			valueList.add(globalValue); 
	                			noFlagValue = "" ;//no flag 放空值	                			        		
		                	}else {
		                		valueList.add(recordList.get(0).getGlobalValue());
		                		if(recordPortMap.get(currentPort4Unbind) == 1) {
		                			noFlagValue = "no" ;//no flag 放值
		                		}else {
		                			noFlagValue = "" ;//no flag 放空值
		                		}
		                	}              	
		                }   
	                }	                	
	                
	                if(varKey.contains(keyNoFlag)) {
	                	valueList.add(noFlagValue);
	                }
	                
	                newVarValue.add(valueList);
				}
				pVO.setVarValue(newVarValue);
			}
		}
		
		return pVO;
	}
	
	@Override
    public  DeliveryParameterVO checkB4DoIpMacOpenBlockDelivery(DeliveryParameterVO pVO) throws ServiceLayerException {
		//檢核IP MAC 綁定相關，放入global參數
        String blockType = null;
        
        ScriptInfoDAOVO siDAOVO = new ScriptInfoDAOVO();
		siDAOVO.setQueryScriptCode(pVO.getScriptCode());
		ScriptInfo dbEntity = scriptInfoDAO.findScriptInfoByIdOrCode(null, pVO.getScriptCode());
		if(dbEntity == null) {
			log.error("check before provision - script info not exist!!");
			throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
		}
        if(pVO.getScriptCode().startsWith(ScriptType.IP_.toString())) {
        	blockType = BlockType.IP.toString();    
        	
        }else if (pVO.getScriptCode().startsWith(ScriptType.MAC_.toString())) {
        	blockType = BlockType.MAC.toString();
        }
                
		if(StringUtils.isNotBlank(blockType)) {
			List<String> varKey = pVO.getVarKey();
			List<List<String>> varValue = pVO.getVarValue();
			List<List<String>> newVarValue = new ArrayList<List<String>>();
			List<String> deviceIdList = pVO.getDeviceId();
			
			// Step 3.檢核JSON內VarKey與系統內設定的腳本變數欄位是否相符
			final String dbVarKeyJSON = dbEntity.getCheckScriptVariable();
			final List<String> dbVarKeyList = (List<String>) transJSON2Object(dbVarKeyJSON, ArrayList.class);
			String keyGlobal = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "");
			
			if(dbVarKeyList != null && dbVarKeyList.size() > 0 && StringUtils.isNotBlank(keyGlobal) ) {
				
				if(dbVarKeyList.contains(keyGlobal)) {
					varKey.add(keyGlobal);
				}
				
				pVO.setVarKey(varKey);
				
				BlockedRecordVO brVO;           	              
            	List<String> valueList = null;
            	Map<String, String>varMap = null;
            	
				int deviceCount = deviceIdList == null ? 0: deviceIdList.size();
				//逐筆設備替換
				for (int idx = 0; idx < deviceCount; idx++) {
	                varMap = new HashMap<String, String>();
	                valueList = varValue.get(idx);
	                for(int i=0; i < varKey.size() -1; i++) {
	                	varMap.put(varKey.get(i), valueList.get(i));
                	}

	                brVO = new BlockedRecordVO();  
	                brVO.setQueryDeviceId(deviceIdList.get(idx));
	                brVO.setQueryBlockType(blockType);
					brVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
					
	                if(varKey.contains(keyGlobal)) {
	                	
		                //綁定行為時，計算同設備資料筆數放入Entry Num
		                if(StringUtils.isNotBlank(dbEntity.getUndoScriptCode())) {                
								List<ModuleBlockedList> bList = findModuleBlockedList(brVO);
								
								//從250往回設定
								for ( int i = 250; i <= 1 ; i--) {
									boolean checkFlag = true;
									for(ModuleBlockedList record : bList) {
										if(StringUtils.equals(record.getGlobalValue(), String.valueOf(i))) {
											checkFlag = false;
											break;
										}
										
									}
									if(checkFlag) {
										valueList.add( String.valueOf(i));
										break;
									}
								}
		                }else {
		                	//解除綁定，放入紀錄中global Value 的Entry Num
		                	brVO.setQueryMacAddress(varMap.get(Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK.replaceAll(Env.SCRIPT_VAR_KEY_SYMBOL, "")));
		                	List<ModuleBlockedList> recordList = findModuleBlockedList(brVO);
		                	
		                	if(recordList != null && recordList.size() >0) {
		                		valueList.add(recordList.get(0).getGlobalValue());
		                	}else {
		                		throw new ServiceLayerException("供裝前系統檢核不通過，請重新操作；若仍再次出現此訊息，請與系統維護商聯繫");
		                	}
		                }   
	                }
	                newVarValue.add(valueList);
				}
				pVO.setVarValue(newVarValue);
			}
		}
		
		return pVO;
	}
}
