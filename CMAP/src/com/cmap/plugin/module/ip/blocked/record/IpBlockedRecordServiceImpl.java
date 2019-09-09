package com.cmap.plugin.module.ip.blocked.record;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.i18n.DatabaseMessageSourceBase;
import com.cmap.model.DeviceList;
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

            ModuleBlockedIpList bilEntity;
            DeviceList dlEntity;
            IpBlockedRecordVO vo;
            for (Object[] entity : entities) {
                bilEntity = (ModuleBlockedIpList)entity[0];
                dlEntity = (DeviceList)entity[1];
                vo = new IpBlockedRecordVO();

                BeanUtils.copyProperties(bilEntity, vo);
                vo.setBlockTimeStr(bilEntity.getBlockTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getBlockTime()) : null);
                vo.setOpenTimeStr(bilEntity.getOpenTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getOpenTime()) : null);
                vo.setUpdateTimeStr(bilEntity.getUpdateTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getUpdateTime()) : null);
                vo.setGroupName(dlEntity.getGroupName());
                vo.setStatusFlag(StringUtils.equals(bilEntity.getStatusFlag(), Constants.STATUS_FLAG_BLOCK)
                                    ? msgBlock
                                    : StringUtils.equals(bilEntity.getStatusFlag(), Constants.STATUS_FLAG_OPEN)
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
                            ipRecordDAO.updateEntity(lastestRecord);

                            // Step 2. 再寫入一筆新的紀錄
                            ModuleBlockedIpList newRecord = transVO2Model(ibrVO);
                            newRecord.setBlockTime(currentTimestamp());
                            ipRecordDAO.insertEntity(newRecord);

                        } else if (StringUtils.equals(actionStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                            // B → O
                            // 更新前一筆紀錄狀態為「O」
                            lastestRecord.setStatusFlag(Constants.STATUS_FLAG_OPEN);
                            lastestRecord.setOpenTime(currentTimestamp());
                            lastestRecord.setOpenBy(currentUserName());
                            lastestRecord.setOpenReason(ibrVO.getOpenReason());
                            lastestRecord.setUpdateTime(currentTimestamp());
                            lastestRecord.setUpdateBy(currentUserName());
                            ipRecordDAO.updateEntity(lastestRecord);
                        }

                    } else if (StringUtils.equals(preStatusFlag, Constants.STATUS_FLAG_OPEN)) {
                        // O → B 、 O → O
                        // 寫入一筆新的紀錄
                        ModuleBlockedIpList newRecord = transVO2Model(ibrVO);
                        newRecord.setOpenTime(currentTimestamp());
                        ipRecordDAO.insertEntity(newRecord);
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

                    ipRecordDAO.insertEntity(newRecord);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增或更新封鎖紀錄時異常! (ModuleBlockedIpList)");
        }
    }
}
