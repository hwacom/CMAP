package com.cmap.plugin.module.ip.mac.bound.record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
import com.cmap.dao.BaseDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipMacRecordService")
@Transactional
public class IpMacBoundRecordServiceImpl extends CommonServiceImpl implements IpMacBoundRecordService {
    @Log
    private static Logger log;

    @Autowired
    private IpMacBoundRecordDAO ipMacRecordDAO;

    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleIpMacBoundList(IpMacBoundRecordVO ibrVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = ipMacRecordDAO.countModuleIpMacBoundList(ibrVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<IpMacBoundRecordVO> findModuleIpMacBoundList(IpMacBoundRecordVO ibrVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<IpMacBoundRecordVO> retList = new ArrayList<>();
        try {
            String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
            String msgOpen = messageSource.getMessage("status.flag.open", Locale.TAIWAN, null);         // O-開通
            String msgUnknown = messageSource.getMessage("status.flag.unknown", Locale.TAIWAN, null);   // U-未知

            List<Object[]> entities = ipMacRecordDAO.findModuleIpMacBoundList(ibrVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                throw new ServiceLayerException("查無資料!");
            }

            IpMacBoundRecordVO vo;
            for (Object[] entity : entities) {
                String groupId = Objects.toString(entity[0]);
                String groupName = Objects.toString(entity[1]);
                String ipAddress = Objects.toString(entity[2]);
                String ipDesc = Objects.toString(entity[3], Env.IP_DESC_NULL_SHOW_WHAT);
                String macAddress = Objects.toString(entity[4]);
                String port = Objects.toString(entity[5]);
                String globalValue = Objects.toString(entity[6]);
                String statusFlag = Objects.toString(entity[7]);
                Timestamp blockTime = entity[8] != null ? (Timestamp)entity[8] : null;
                String blockBy = Objects.toString(entity[9], null);
                String blockReason = Objects.toString(entity[10], null);
                Timestamp openTime = entity[11] != null ? (Timestamp)entity[11] : null;
                String openBy = Objects.toString(entity[12], null);
                String openReason = Objects.toString(entity[13], null);
                Timestamp updateTime = entity[14] != null ? (Timestamp)entity[14] : null;
                String updateBy = Objects.toString(entity[15], null);
                String listId = Objects.toString(entity[16]);
                String deviceId = Objects.toString(entity[17]);
                String scriptCode = Objects.toString(entity[18]);
                String scriptName = Objects.toString(entity[19]);

                vo = new IpMacBoundRecordVO();
                vo.setListId(listId);
                vo.setGroupId(groupId);
                vo.setGroupName(groupName);
                vo.setDeviceId(deviceId);
                vo.setIpAddress(ipAddress);
                vo.setIpDesc(ipDesc);
                vo.setMacAddress(macAddress);
                vo.setPort(port);
                vo.setGlobalValue(globalValue);
                vo.setBlockTimeStr(blockTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(blockTime) : null);
                vo.setBlockBy(blockBy);
                vo.setBlockReason(blockReason);
                vo.setOpenTimeStr(openTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(openTime) : null);
                vo.setOpenBy(openBy);
                vo.setOpenReason(openReason);
                vo.setUpdateTimeStr(updateTime != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(updateTime) : null);
                vo.setUpdateBy(updateBy);
                vo.setStatusFlag(StringUtils.equals(statusFlag, Constants.STATUS_FLAG_BLOCK)
                                    ? msgBlock
                                    : StringUtils.equals(statusFlag, Constants.STATUS_FLAG_OPEN)
                                        ? msgOpen
                                        : msgUnknown);
                vo.setScriptCode(scriptCode);
                vo.setScriptName(scriptName);
                
                retList.add(vo);
            }

        } catch (ServiceLayerException sle) {
            log.error(sle.toString(), sle);
            throw sle;

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retList;
    }

    private ModuleIpMacBoundList transVO2Model(IpMacBoundRecordVO ibrVO) {
        ModuleIpMacBoundList entity = new ModuleIpMacBoundList();
        BeanUtils.copyProperties(ibrVO, entity);
        entity.setCreateTime(currentTimestamp());
        entity.setCreateBy(currentUserName());
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    @Override
    public void saveOrUpdateRecord(List<IpMacBoundRecordVO> ibrVOs) throws ServiceLayerException {
        try {
            IpMacBoundRecordVO qVO;
            for (IpMacBoundRecordVO ibrVO : ibrVOs) {
                qVO = new IpMacBoundRecordVO();
                qVO.setQueryGroupId(ibrVO.getGroupId());
                qVO.setQueryDeviceId(ibrVO.getDeviceId());
                qVO.setQueryIpAddress(ibrVO.getIpAddress());
                qVO.setQueryMacAddress(ibrVO.getMacAddress());
//                qVO.setQueryExcludeStatusFlag(Arrays.asList(Constants.STATUS_FLAG_UNKNOWN, Constants.STATUS_FLAG_OPEN)); // 狀態U/O的不查

                String actionStatusFlag = ibrVO.getStatusFlag();
                String preStatusFlag = "";

                ModuleIpMacBoundList lastestRecord = ipMacRecordDAO.findLastestModuleIpMacBoundList(qVO);

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
//                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_UNKNOWN);
//                            lastestRecord.setOpenReason("無系統解鎖紀錄");
//                            lastestRecord.setRemark("無系統解鎖紀錄");
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            ipMacRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);

                            // Step 2. 再寫入一筆新的紀錄
//                            ModuleBlockedIpList newRecord = transVO2Model(ibrVO);
//                            newRecord.setBlockTime(currentTimestamp());
//                            ipMacRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);

                        } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                            // B → O
                            // 更新前一筆紀錄狀態為「O」
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_OPEN);
                            lastestRecord.setOpenTime(currentTimestamp());
                            lastestRecord.setOpenBy(currentUserName());
                            lastestRecord.setOpenReason(ibrVO.getOpenReason());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            ipMacRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                        }

                    } else if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        // O → B 、 O → O
                        // 寫入一筆新的紀錄
                    	if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                    		ModuleIpMacBoundList newRecord = transVO2Model(ibrVO);
                            newRecord.setBlockTime(currentTimestamp());
                            newRecord.setBlockBy(currentUserName());
                            ipMacRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                            
                    	}else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                    		lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            ipMacRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                    	}
                        
                    }

                } else {
                    /*
                     * 沒查到資料 => 寫入一筆新的紀錄
                     */
                    ModuleIpMacBoundList newRecord = transVO2Model(ibrVO);

                    if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                        newRecord.setBlockTime(currentTimestamp());

                    } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        newRecord.setOpenTime(currentTimestamp());
                    }

                    ipMacRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新封鎖紀錄時異常! (ModuleIpMacBoundList)");
        }
    }
    /*
    @Override
    public IpMacBoundRecordVO checkIpMacBoundList(String deviceId, String ipAddress, String macAddress , List<IpMacBoundRecordVO> dbRecordList) {
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
    	String sycReason = messageSource.getMessage("synchronize.switch.ip", Locale.TAIWAN, null);
    	
		for (IpMacBoundRecordVO recVO : dbRecordList) {
			if (recVO.getDeviceId().equals(deviceId)
					&& recVO.getIpAddress().equals(ipAddress)
					&& (recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock))) {
				log.debug("checkIpMacBoundList ==> 設備同步資訊比對相同，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + ipAddress + "，block_by," + recVO.getBlockBy());
				return recVO;
			}
			
		}
		
		IpMacBoundRecordVO ibrVO = new IpMacBoundRecordVO();
		ibrVO.setDeviceId(deviceId);
		ibrVO.setIpAddress(ipAddress);
		ibrVO.setMacAddress(macAddress);
		ibrVO.setStatusFlag(msgBlock);
		ibrVO.setBlockReason(sycReason);
		ibrVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
		
		return ibrVO;
	}
    
    @Override
    public List<IpMacBoundRecordVO> compareIpMacBoundList(List<IpMacBoundRecordVO> dbRecordList, Map<String, IpMacBoundRecordVO> compareMap) {
    	
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖    	
        List<IpMacBoundRecordVO> resultList = new ArrayList<IpMacBoundRecordVO>();
        
		for (IpMacBoundRecordVO recVO : dbRecordList) {
			//不存在同步結果清單中的自動解鎖
			if(!compareMap.containsKey(recVO.getDeviceId()+recVO.getIpAddress())) {
				log.debug("compareIpMacBoundList ==> 不存在同步結果清單中，" + recVO.getGroupId() + ", "
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
	}*/
}
