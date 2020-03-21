package com.cmap.plugin.module.blocked.record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.cmap.dao.BaseDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.service.ScriptService;
import com.cmap.service.impl.CommonServiceImpl;
import com.cmap.service.vo.ConfigInfoVO;
import com.cmap.service.vo.ScriptServiceVO;

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
                Timestamp blockTime = entity[12] != null ? (Timestamp)entity[12] : null;
                String blockBy = Objects.toString(entity[13], null);
                String blockReason = Objects.toString(entity[14], null);
                Timestamp openTime = entity[15] != null ? (Timestamp)entity[15] : null;
                String openBy = Objects.toString(entity[16], null);
                String openReason = Objects.toString(entity[17], null);
                Timestamp updateTime = entity[18] != null ? (Timestamp)entity[18] : null;
                String updateBy = Objects.toString(entity[19], null);
                String listId = Objects.toString(entity[20]);
                
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
    
    /**
     * 寫入 IP MAC 綁定 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    @Override
    public void writeModuleIpMacBoundListRecord(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String actionStatusFlag, String remark) throws ServiceLayerException {
        String groupId = ciVO.getGroupId();
        String deviceId = ciVO.getDeviceId();
        
        // 定義IP封鎖腳本中「IP_Address」的變數名稱 for 寫入異動紀錄table使用
        String ipAddressVarKey = Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK;
        String macAddressVarKey = Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK;
        String interfaceVarKey = Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK;
        String globalVarKey = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING;
        String globalValue = null;
        
        List<BlockedRecordVO> brVOs = new ArrayList<>();
        BlockedRecordVO brVO = null;
        List<String> keepValue = new ArrayList<>();
        
        for (Map<String, String> varMap : varMapList) {
            String ipAddress = varMap.get(ipAddressVarKey);
            String macAddress = varMap.get(macAddressVarKey);
            if (StringUtils.isBlank(ipAddress)) {
                throw new ServiceLayerException("系統參數異常無法執行，請重新操作! (ipAddress為空)");
            }
            if (StringUtils.isBlank(macAddress)) {
                throw new ServiceLayerException("系統參數異常無法執行，請重新操作! (macAddress為空)");
            }
                        
            brVO = new BlockedRecordVO();
            brVO.setGroupId(groupId);
            brVO.setDeviceId(deviceId);
            brVO.setIpAddress(ipAddress);
            brVO.setMacAddress(macAddress);
            if(varMap.containsKey(interfaceVarKey)) {
            	brVO.setPort(varMap.get(interfaceVarKey));
            }
            if(varMap.containsKey(globalVarKey)) {
            	brVO.setGlobalValue(varMap.get(globalVarKey));
            	globalValue = varMap.get(globalVarKey);
            }
            if(varMap.containsKey(Env.KEY_VAL_OF_NO_FLAG_WITH_CMD)) {
            	if(StringUtils.isNotBlank(varMap.get(Env.KEY_VAL_OF_NO_FLAG_WITH_CMD))) {
            		globalValue = "";
            	}
            }
            
            brVO.setStatusFlag(actionStatusFlag);

            if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                brVO.setBlockBy(currentUserName());
                brVO.setBlockReason(remark);
                
            } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                brVO.setOpenBy(currentUserName());
                brVO.setOpenReason(remark);
            }

            brVO.setScriptCode(scriptCode);
            ScriptServiceVO vo = scriptService.getScriptInfoByScriptCode(scriptCode);
            if(vo != null) {
            	brVO.setScriptName(vo.getScriptName());
            }
            brVO.setRemark(remark);
            brVO.setBlockType(BlockType.IP_MAC.toString());
            
            keepValue.add(brVO.getIpAddress()+brVO.getMacAddress());            
            brVOs.add(brVO);
        }

        if(globalValue != null) {
        	//查詢同設備封鎖紀錄For更新GlobalValue使用
            BlockedRecordVO searchIbrVO = new BlockedRecordVO();
            searchIbrVO.setQueryDeviceId(deviceId);
            searchIbrVO.setQueryStatusFlag(Arrays.asList(Constants.STATUS_FLAG_BLOCK));
            List<ModuleBlockedList> recordList = findModuleBlockedList(searchIbrVO);
            
            for(ModuleBlockedList entity:recordList) {
            	if(!keepValue.contains(entity.getIpAddress()+entity.getMacAddress())) {
            		entity.setGlobalValue(globalValue);            		
                	brVOs.add(transModel2VO(entity));
            	}
            }
        }        
        
        if (brVOs != null && !brVOs.isEmpty()) {
        	saveOrUpdateRecord(brVOs);
        }
    }
    

    @Override
    public void writeModuleBlockMacListRecord(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String actionStatusFlag, String remark) throws ServiceLayerException {
        String groupId = ciVO.getGroupId();
        String deviceId = ciVO.getDeviceId();

        String macAddressVarKey = Env.KEY_VAL_OF_MAC_ADDR_WITH_MAC_OPEN_BLOCK;
        String interfaceVarKey = Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK;
        String globalVarKey = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING;
        
        List<BlockedRecordVO> brVOs = new ArrayList<>();
        BlockedRecordVO brVO = null;
        for (Map<String, String> varMap : varMapList) {
            String macAddress = varMap.get(macAddressVarKey);
            if (StringUtils.isBlank(macAddress)) {
                throw new ServiceLayerException("系統參數異常無法執行，請重新操作! (macAddress為空)");
            }

            brVO = new BlockedRecordVO();
            brVO.setGroupId(groupId);
            brVO.setDeviceId(deviceId);
            brVO.setMacAddress(macAddress);
            if(varMap.containsKey(interfaceVarKey)) {
            	brVO.setPort(varMap.get(interfaceVarKey));
            }
            if(varMap.containsKey(globalVarKey)) {
            	brVO.setGlobalValue(varMap.get(globalVarKey));
            }
            brVO.setStatusFlag(actionStatusFlag);
            
            if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                brVO.setBlockBy(currentUserName());
                brVO.setBlockReason(remark);

            } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                brVO.setOpenBy(currentUserName());
                brVO.setOpenReason(remark);
            }

            brVO.setScriptCode(scriptCode);
            ScriptServiceVO vo = scriptService.getScriptInfoByScriptCode(scriptCode);
            if(vo != null) {
            	brVO.setScriptName(vo.getScriptName());
            }
            brVO.setRemark(remark);
            brVO.setBlockType(BlockType.MAC.toString());
            
            brVOs.add(brVO);
        }

        if (brVOs != null && !brVOs.isEmpty()) {
            saveOrUpdateRecord(brVOs);
        }
    }

    /**
     * 寫入 Port封鎖 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    @Override
    public void writeModuleBlockPortListRecord(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String actionStatusFlag, String remark) throws ServiceLayerException {
        String groupId = ciVO.getGroupId();
        String deviceId = ciVO.getDeviceId();

        // 定義IP封鎖腳本中「Port」的變數名稱 for 寫入異動紀錄table使用
        String portIdVarKey = Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK;
        String globalVarKey = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING;
        
        List<BlockedRecordVO> brVOs = new ArrayList<>();
        BlockedRecordVO brVO = null;
        for (Map<String, String> varMap : varMapList) {
            String portId = varMap.get(portIdVarKey);
            if (StringUtils.isBlank(portId)) {
                throw new ServiceLayerException("系統參數異常無法執行，請重新操作! (portId為空)");
            }

            brVO = new BlockedRecordVO();
            brVO.setGroupId(groupId);
            brVO.setDeviceId(deviceId);
            brVO.setPort(portId);
            brVO.setStatusFlag(actionStatusFlag);

            if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                brVO.setBlockBy(currentUserName());
                brVO.setBlockReason(remark);

            } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                brVO.setOpenBy(currentUserName());
                brVO.setOpenReason(remark);
            }

            brVO.setScriptCode(scriptCode);
            ScriptServiceVO vo = scriptService.getScriptInfoByScriptCode(scriptCode);
            if(vo != null) {
            	brVO.setScriptName(vo.getScriptName());
            }
            brVO.setRemark(remark);
            brVO.setBlockType(BlockType.PORT.toString());
            
            brVOs.add(brVO);
        }

        if (brVOs != null && !brVOs.isEmpty()) {
            saveOrUpdateRecord(brVOs);
        }
    }
    

    /**
     * 寫入 IP封鎖 紀錄資料
     * @param ciVO
     * @param scriptCode
     * @param varMapList
     * @param actionStatusFlag
     * @param remark
     * @throws ServiceLayerException
     */
    @Override
    public void writeModuleBlockIpListRecord(
            ConfigInfoVO ciVO, String scriptCode, List<Map<String, String>> varMapList, String actionStatusFlag, String remark) throws ServiceLayerException {
        String groupId = ciVO.getGroupId();
        String deviceId = ciVO.getDeviceId();

        // 定義IP封鎖腳本中「IP_Address」的變數名稱 for 寫入異動紀錄table使用
        String ipAddressVarKey = Env.KEY_VAL_OF_IP_ADDR_WITH_IP_OPEN_BLOCK;
        String portIdVarKey = Env.KEY_VAL_OF_PORT_ID_WITH_PORT_OPEN_BLOCK;
        String globalVarKey = Env.KEY_VAL_OF_GLOBAL_VALUE_WITH_IP_MAC_BINDING;
        
        List<BlockedRecordVO> brVOs = new ArrayList<>();
        BlockedRecordVO brVO = null;
        for (Map<String, String> varMap : varMapList) {
            String ipAddress = varMap.get(ipAddressVarKey);
            if (StringUtils.isBlank(ipAddress)) {
                throw new ServiceLayerException("系統參數異常無法執行，請重新操作! (ipAddress為空)");
            }

            brVO = new BlockedRecordVO();
            brVO.setGroupId(groupId);
            brVO.setDeviceId(deviceId);
            brVO.setIpAddress(ipAddress);
            if(varMap.containsKey(portIdVarKey)) {
            	brVO.setPort(varMap.get(portIdVarKey));
            }
            if(varMap.containsKey(globalVarKey)) {
            	brVO.setGlobalValue(varMap.get(globalVarKey));
            }
            brVO.setStatusFlag(actionStatusFlag);

            if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                brVO.setBlockBy(currentUserName());
                brVO.setBlockReason(remark);

            } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                brVO.setOpenBy(currentUserName());
                brVO.setOpenReason(remark);
            }

            brVO.setScriptCode(scriptCode);
            ScriptServiceVO vo = scriptService.getScriptInfoByScriptCode(scriptCode);
            if(vo != null) {
            	brVO.setScriptName(vo.getScriptName());
            }
            brVO.setRemark(remark);
            brVO.setBlockType(BlockType.IP.toString());
            
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
}
