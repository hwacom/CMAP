package com.cmap.plugin.module.ip.maintain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.BaseDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipMaintainService")
public class IpMaintainServiceImpl extends CommonServiceImpl implements IpMaintainService {
    @Log
    private static Logger log;

    @Autowired
    private IpMaintainDAO ipMaintainDAO;

    @Override
    public long countIpDataSetting(IpMaintainServiceVO imsVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = ipMaintainDAO.countModuleIpDataSetting(imsVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVal;
    }

    @Override
    public List<IpMaintainServiceVO> findIpDataSetting(IpMaintainServiceVO imsVO)
            throws ServiceLayerException {
        List<IpMaintainServiceVO> retList = new ArrayList<>();
        try {
            List<Object[]> entities = ipMaintainDAO.findModuleIpDataSetting(imsVO, imsVO.getStartNum(), imsVO.getPageLength());

            if (entities != null && !entities.isEmpty()) {

                ModuleIpDataSetting midsEntity;
                String groupName;
                IpMaintainServiceVO vo;

                for (Object[] obj : entities) {
                    midsEntity = (ModuleIpDataSetting)obj[0];
                    groupName = Objects.toString(obj[1], "N/A");

                    vo = new IpMaintainServiceVO();
                    BeanUtils.copyProperties(midsEntity, vo);
                    vo.setGroupName(groupName);

                    retList.add(vo);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retList;
    }

    @Override
    public IpMaintainServiceVO addIpDataSetting(List<IpMaintainServiceVO> addList)
            throws ServiceLayerException {
        IpMaintainServiceVO retVO = new IpMaintainServiceVO();
        try {
            List<ModuleIpDataSetting> add1Entities = new ArrayList<>();
            List<ModuleIpDataSetting> update1Entities = new ArrayList<>();

            List<ModuleIpDataSetting> add2Entities = new ArrayList<>();
            List<ModuleIpDataSetting> update2Entities = new ArrayList<>();

            ModuleIpDataSetting entity;

            // Step 1. 先剔除重複資料，重複資料已最後一筆為主
            String key;
            Map<String, IpMaintainServiceVO> uniqueMap = new HashMap<>();
            for (IpMaintainServiceVO imsVO : addList) {
                key = imsVO.getGroupId().concat(Env.COMM_SEPARATE_SYMBOL).concat(imsVO.getModifyIpAddr()); // Key = GroupId@~IpAddr
                uniqueMap.put(key, imsVO); // 不做判斷，一律已後面的資料覆蓋前面已存在的key-value
            }

            if (uniqueMap != null && !uniqueMap.isEmpty()) {
                List<Object[]> qResultList;
                IpMaintainServiceVO qVO;
                for (IpMaintainServiceVO imsVO : uniqueMap.values()) {
                    // Step 2. 檢核 Group_ID + IP_ADDR 是否已存在，是的話做更新；否則新增
                    qVO = new IpMaintainServiceVO();
                    qVO.setQueryGroup(imsVO.getGroupId());
                    qVO.setQueryIp(imsVO.getModifyIpAddr());
                    qResultList = ipMaintainDAO.findModuleIpDataSetting(qVO, null, null);

                    if (qResultList != null && !qResultList.isEmpty()) {
                        // 存在 => 更新
                        entity = (ModuleIpDataSetting)qResultList.get(0)[0];
                        entity.setIpDesc(imsVO.getModifyIpDesc());
                        entity.setUpdateTime(currentTimestamp());
                        entity.setUpdateBy(currentUserName());
                        update1Entities.add(entity);

                    } else {
                        // 不存在 => 新增
                        entity = new ModuleIpDataSetting();
                        entity.setGroupId(imsVO.getGroupId());
                        entity.setIpAddr(imsVO.getModifyIpAddr());
                        entity.setIpDesc(imsVO.getModifyIpDesc());
                        entity.setCreateTime(currentTimestamp());
                        entity.setCreateBy(currentUserName());
                        entity.setUpdateTime(currentTimestamp());
                        entity.setUpdateBy(currentUserName());
                        add1Entities.add(entity);
                    }

                    // Step 2-2. 檢核 Group_ID + IP_ADDR 是否已存在Secondary DB，是的話做更新；否則新增
                    qVO = new IpMaintainServiceVO();
                    qVO.setQueryGroup(imsVO.getGroupId());
                    qVO.setQueryIp(imsVO.getModifyIpAddr());
                    qResultList = ipMaintainDAO.findModuleIpDataSettingFromSecondaryDB(qVO, null, null);

                    if (qResultList != null && !qResultList.isEmpty()) {
                        // 存在 => 更新
                        entity = (ModuleIpDataSetting)qResultList.get(0)[0];
                        entity.setIpDesc(imsVO.getModifyIpDesc());
                        entity.setUpdateTime(currentTimestamp());
                        entity.setUpdateBy(currentUserName());
                        update2Entities.add(entity);

                    } else {
                        // 不存在 => 新增
                        entity = new ModuleIpDataSetting();
                        entity.setGroupId(imsVO.getGroupId());
                        entity.setIpAddr(imsVO.getModifyIpAddr());
                        entity.setIpDesc(imsVO.getModifyIpDesc());
                        entity.setCreateTime(currentTimestamp());
                        entity.setCreateBy(currentUserName());
                        entity.setUpdateTime(currentTimestamp());
                        entity.setUpdateBy(currentUserName());
                        add2Entities.add(entity);
                    }
                }

                if (add1Entities != null && !add1Entities.isEmpty()) {
                    ipMaintainDAO.insertEntities(BaseDAO.TARGET_PRIMARY_DB, add1Entities);
                }
                if (update1Entities != null && !update1Entities.isEmpty()) {
                    ipMaintainDAO.updateEntities(BaseDAO.TARGET_PRIMARY_DB, update1Entities);
                }

                if (add2Entities != null && !add2Entities.isEmpty()) {
                    ipMaintainDAO.insertEntities2Secondary(add2Entities);
                }
                if (update2Entities != null && !update2Entities.isEmpty()) {
                    ipMaintainDAO.updateEntities2Secondary(update2Entities);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("新增失敗");
        }
        return retVO;
    }

    @Override
    public IpMaintainServiceVO updateIpDataSetting(List<IpMaintainServiceVO> updateList)
            throws ServiceLayerException {
        IpMaintainServiceVO retVO = new IpMaintainServiceVO();
        try {
            List<ModuleIpDataSetting> updateEntities = new ArrayList<>();
            ModuleIpDataSetting entity;
            String settingId;
            String modifyIpDesc;
            for (IpMaintainServiceVO imsVO : updateList) {
                settingId = imsVO.getSettingId();

                entity = ipMaintainDAO.findModuleIpDataSettingById(settingId);

                if (entity == null) {
                    continue;

                } else {
                    modifyIpDesc = imsVO.getModifyIpDesc();
                    entity.setIpDesc(modifyIpDesc);
                    entity.setUpdateTime(currentTimestamp());
                    entity.setUpdateBy(currentUserName());
                    updateEntities.add(entity);
                }
            }

            if (updateEntities != null && !updateEntities.isEmpty()) {
                ipMaintainDAO.updateEntities(BaseDAO.TARGET_ALL_DB, updateEntities);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("修改失敗");
        }
        return retVO;
    }

    @Override
    public IpMaintainServiceVO deleteIpDataSetting(List<IpMaintainServiceVO> deleteList)
            throws ServiceLayerException {
        IpMaintainServiceVO retVO = new IpMaintainServiceVO();
        try {
            List<ModuleIpDataSetting> deleteEntities = new ArrayList<>();
            ModuleIpDataSetting entity;
            String settingId;
            for (IpMaintainServiceVO imsVO : deleteList) {
                settingId = imsVO.getSettingId();

                entity = ipMaintainDAO.findModuleIpDataSettingById(settingId);

                if (entity == null) {
                    continue;

                } else {
                    deleteEntities.add(entity);
                }
            }

            if (deleteEntities != null && !deleteEntities.isEmpty()) {
                ipMaintainDAO.deleteEntities(BaseDAO.TARGET_ALL_DB, deleteEntities);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("刪除失敗");
        }
        return retVO;
    }
}
