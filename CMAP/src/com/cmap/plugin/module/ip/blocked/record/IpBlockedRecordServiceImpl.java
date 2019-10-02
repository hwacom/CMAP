package com.cmap.plugin.module.ip.blocked.record;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

@Service("ipRecordService")
@Transactional
public class IpBlockedRecordServiceImpl extends CommonServiceImpl implements IpBlockedRecordService {
    @Log
    private static Logger log;

    @Autowired
    private IpBlockedRecordDAO ipRecordDAO;

    @Autowired
    private DatabaseMessageSourceBase messageSource;

    @Override
    public long countModuleBlockedIpList(IpBlockedRecordVO ibrVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = ipRecordDAO.countModuleBlockedIpList(ibrVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<IpBlockedRecordVO> findModuleBlockedIpList(IpBlockedRecordVO ibrVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<IpBlockedRecordVO> retList = new ArrayList<>();
        try {
            String msgBlock = messageSource.getMessage("status.flag.block", Locale.TAIWAN, null);       // B-封鎖
            String msgOpen = messageSource.getMessage("status.flag.open", Locale.TAIWAN, null);         // O-開通
            String msgUnknown = messageSource.getMessage("status.flag.unknown", Locale.TAIWAN, null);   // U-未知

            List<Object[]> entities = ipRecordDAO.findModuleBlockedIpList(ibrVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                throw new ServiceLayerException("查無資料!");
            }

            IpBlockedRecordVO vo;
            for (Object[] entity : entities) {
                String groupId = Objects.toString(entity[0]);
                String groupName = Objects.toString(entity[1]);
                String ipAddress = Objects.toString(entity[2]);
                String ipDesc = Objects.toString(entity[3], Env.IP_DESC_NULL_SHOW_WHAT);
                String statusFlag = Objects.toString(entity[4]);
                Timestamp blockTime = entity[5] != null ? (Timestamp)entity[5] : null;
                String blockBy = Objects.toString(entity[6]);
                String blockReason = Objects.toString(entity[7]);
                Timestamp openTime = entity[8] != null ? (Timestamp)entity[8] : null;
                String openBy = Objects.toString(entity[9]);
                String openReason = Objects.toString(entity[10]);
                Timestamp updateTime = entity[11] != null ? (Timestamp)entity[11] : null;
                String updateBy = Objects.toString(entity[12]);
                String listId = Objects.toString(entity[13]);
                String deviceId = Objects.toString(entity[14]);

                vo = new IpBlockedRecordVO();
                vo.setListId(listId);
                vo.setGroupId(groupId);
                vo.setGroupName(groupName);
                vo.setDeviceId(deviceId);
                vo.setIpAddress(ipAddress);
                vo.setIpDesc(ipDesc);
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

    private ModuleBlockedIpList transVO2Model(IpBlockedRecordVO ibrVO) {
        ModuleBlockedIpList entity = new ModuleBlockedIpList();
        BeanUtils.copyProperties(ibrVO, entity);
        entity.setCreateTime(currentTimestamp());
        entity.setCreateBy(currentUserName());
        entity.setUpdateTime(currentTimestamp());
        entity.setUpdateBy(currentUserName());
        return entity;
    }

    @Override
    public void saveOrUpdateRecord(List<IpBlockedRecordVO> ibrVOs) throws ServiceLayerException {
        try {
            IpBlockedRecordVO qVO;
            for (IpBlockedRecordVO ibrVO : ibrVOs) {
                qVO = new IpBlockedRecordVO();
                qVO.setQueryGroupId(ibrVO.getGroupId());
                qVO.setQueryDeviceId(ibrVO.getDeviceId());
                qVO.setQueryIpAddress(ibrVO.getIpAddress());
                qVO.setQueryExcludeStatusFlag(Arrays.asList(Constants.STATUS_FLAG_UNKNOWN, Constants.STATUS_FLAG_OPEN)); // 狀態U/O的不查

                String actionStatusFlag = ibrVO.getStatusFlag();
                String preStatusFlag = "";

                ModuleBlockedIpList lastestRecord = ipRecordDAO.findLastestModuleBlockedIpList(qVO);

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
                            ipRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);

                            // Step 2. 再寫入一筆新的紀錄
                            ModuleBlockedIpList newRecord = transVO2Model(ibrVO);
                            newRecord.setBlockTime(currentTimestamp());
                            ipRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);

                        } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                            // B → O
                            // 更新前一筆紀錄狀態為「O」
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_OPEN);
                            lastestRecord.setOpenTime(currentTimestamp());
                            lastestRecord.setOpenBy(currentUserName());
                            lastestRecord.setOpenReason(ibrVO.getOpenReason());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            ipRecordDAO.updateEntity(BaseDAO.TARGET_PRIMARY_DB, lastestRecord);
                        }

                    } else if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        // O → B 、 O → O
                        // 寫入一筆新的紀錄
                        ModuleBlockedIpList newRecord = transVO2Model(ibrVO);
                        newRecord.setOpenTime(currentTimestamp());
                        ipRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                    }

                } else {
                    /*
                     * 沒查到資料 => 寫入一筆新的紀錄
                     */
                    ModuleBlockedIpList newRecord = transVO2Model(ibrVO);

                    if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_BLOCK)) {
                        newRecord.setBlockTime(currentTimestamp());

                    } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        newRecord.setOpenTime(currentTimestamp());
                    }

                    ipRecordDAO.insertEntity(BaseDAO.TARGET_PRIMARY_DB, newRecord);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新封鎖紀錄時異常! (ModuleBlockedIpList)");
        }
    }
}
