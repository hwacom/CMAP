package com.cmap.plugin.module.port.blocked.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.cmap.model.DeviceList;
import com.cmap.service.impl.CommonServiceImpl;

@Service("portRecordService")
@Transactional
public class PortBlockedRecordServiceImpl extends CommonServiceImpl implements PortBlockedRecordService {
    @Log
    private static Logger log;

    @Autowired
    private PortBlockedRecordDAO portRecordDAO;

    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleBlockedPortList(PortBlockedRecordVO pbrVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = portRecordDAO.countModuleBlockedPortList(pbrVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<PortBlockedRecordVO> findModuleBlockedPortList(PortBlockedRecordVO pbrVO,
            Integer startRow, Integer pageLength) throws ServiceLayerException {
        List<PortBlockedRecordVO> retList = new ArrayList<>();
        try {
            String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
            String msgOpen = messageSource.getMessage("status.flag.open", Locale.TAIWAN, null);         // O-開通
            String msgUnknown = messageSource.getMessage("status.flag.unknown", Locale.TAIWAN, null);   // U-未知

            List<Object[]> entities = portRecordDAO.findModuleBlockedPortList(pbrVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                throw new ServiceLayerException("查無資料!");
            }

            ModuleBlockedPortList bplEntity;
            DeviceList dlEntity;
            PortBlockedRecordVO vo;
            for (Object[] entity : entities) {
                bplEntity = (ModuleBlockedPortList)entity[0];
                dlEntity = (DeviceList)entity[1];
                vo = new PortBlockedRecordVO();

                BeanUtils.copyProperties(bplEntity, vo);
                vo.setBlockTimeStr(bplEntity.getBlockTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bplEntity.getBlockTime()) : null);
                vo.setOpenTimeStr(bplEntity.getOpenTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bplEntity.getOpenTime()) : null);
                vo.setUpdateTimeStr(bplEntity.getUpdateTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bplEntity.getUpdateTime()) : null);
                vo.setGroupName(dlEntity.getGroupName());
                vo.setDeviceName(dlEntity.getDeviceName());
                vo.setPortName(bplEntity.getPortId());
                vo.setStatusFlag(StringUtils.equals(bplEntity.getStatusFlag(), Constants.STATUS_FLAG_BLOCK)
                                    ? msgBlock
                                    : StringUtils.equals(bplEntity.getStatusFlag(), Constants.STATUS_FLAG_OPEN)
                                        ? msgOpen
                                        : msgUnknown);

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

    private ModuleBlockedPortList transVO2Model(PortBlockedRecordVO pbrVO) {
        ModuleBlockedPortList entity = new ModuleBlockedPortList();
        BeanUtils.copyProperties(pbrVO, entity);
        entity.setCreateTime(currentTimestamp());
        entity.setCreateBy(currentUserName());
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    @Override
    public void saveOrUpdateRecord(List<PortBlockedRecordVO> pbrVOs) throws ServiceLayerException {
        try {
            PortBlockedRecordVO qVO;
            for (PortBlockedRecordVO pbrVO : pbrVOs) {
                qVO = new PortBlockedRecordVO();
                qVO.setQueryGroupId(pbrVO.getGroupId());
                qVO.setQueryDeviceId(pbrVO.getDeviceId());
                qVO.setQueryPortId(pbrVO.getPortId());
                qVO.setQueryExcludeStatusFlag(Arrays.asList(Constants.STATUS_FLAG_UNKNOWN, Constants.STATUS_FLAG_OPEN)); // 狀態U/O的不查

                String actionStatusFlag = pbrVO.getStatusFlag();
                String preStatusFlag = "";

                ModuleBlockedPortList lastestRecord = portRecordDAO.findLastestModuleBlockedPortList(qVO);

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
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_UNKNOWN);
                            lastestRecord.setOpenReason("無系統解鎖紀錄");
                            lastestRecord.setRemark("無系統解鎖紀錄");
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            portRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);

                            // Step 2. 再寫入一筆新的紀錄
                            ModuleBlockedPortList newRecord = transVO2Model(pbrVO);
                            newRecord.setBlockTime(currentTimestamp());
                            portRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);

                        } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                            // B → O
                            // 更新前一筆紀錄狀態為「O」
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_OPEN);
                            lastestRecord.setOpenTime(currentTimestamp());
                            lastestRecord.setOpenBy(currentUserName());
                            lastestRecord.setOpenReason(pbrVO.getOpenReason());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            portRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                        }

                    } else if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        // O → B 、 O → O
                        // 寫入一筆新的紀錄
                        ModuleBlockedPortList newRecord = transVO2Model(pbrVO);
                        newRecord.setOpenTime(currentTimestamp());
                        portRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                    }

                } else {
                    /*
                     * 沒查到資料 => 寫入一筆新的紀錄
                     */
                    ModuleBlockedPortList newRecord = transVO2Model(pbrVO);

                    if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                        newRecord.setBlockTime(currentTimestamp());

                    } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        newRecord.setOpenTime(currentTimestamp());
                    }

                    portRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新封鎖紀錄時異常! (ModuleBlockedPortList)");
        }
    }
    

    @Override
    public PortBlockedRecordVO checkPortBlockedList(String groupId, String deviceId, String portId , List<PortBlockedRecordVO> dbRecordList) {
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
    	String sycReason = messageSource.getMessage("synchronize.switch.port", Locale.TAIWAN, null);
    	
		for (PortBlockedRecordVO recVO : dbRecordList) {
			if (recVO.getGroupId().equals(groupId) && recVO.getDeviceId().equals(deviceId)
					&& recVO.getPortName().equals(portId)
					&& (recVO.getStatusFlag().equals(Constants.STATUS_FLAG_BLOCK) || recVO.getStatusFlag().equals(msgBlock))) {
				log.debug("IpBlockedRecord ==> 設備同步資訊比對相同，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + portId + "，block_by," + recVO.getBlockBy());
				return recVO;
			}
			
		}
		
		PortBlockedRecordVO pbrVO = new PortBlockedRecordVO();
		pbrVO.setGroupId(groupId);
		pbrVO.setDeviceId(deviceId);
		pbrVO.setPortId(portId);
		pbrVO.setStatusFlag(msgBlock);
		pbrVO.setBlockReason(sycReason);
		pbrVO.setStatusFlag(Constants.STATUS_FLAG_BLOCK);
		
		return pbrVO;
	}
    
    @Override
    public List<PortBlockedRecordVO> comparePortBlockedList(List<PortBlockedRecordVO> dbRecordList, Map<String, PortBlockedRecordVO> compareMap) {
    	
    	String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
        List<PortBlockedRecordVO> resultList = new ArrayList<PortBlockedRecordVO>();
        
		for (PortBlockedRecordVO recVO : dbRecordList) {
			//不存在同步結果清單中的自動解鎖
			if(!compareMap.containsKey(recVO.getDeviceId()+recVO.getPortId())) {
				log.debug("comparePortblockedList ==> 不存在同步結果清單中，" + recVO.getGroupId() + ", "
						+ recVO.getDeviceId() + ", " + recVO.getPortId() + "，block_by," + recVO.getBlockBy());
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
