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
import org.springframework.transaction.annotation.Transactional;

import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.BaseDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipMaintainService")
@Transactional
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

            // Step 1. 先剔除重複資料，重複資料已最後一筆為主
            String key;
            Map<String, IpMaintainServiceVO> uniqueMap = new HashMap<>();
            for (IpMaintainServiceVO imsVO : addList) {
                key = imsVO.getGroupId().concat(Env.COMM_SEPARATE_SYMBOL).concat(imsVO.getModifyIpAddr()); // Key = GroupId@~IpAddr
                uniqueMap.put(key, imsVO); // 不做判斷，一律已後面的資料覆蓋前面已存在的key-value
            }

            if (uniqueMap != null && !uniqueMap.isEmpty()) {
                ModuleIpDataSetting entity1;
                ModuleIpDataSetting entity2;

                String groupId;
                String ipAddr;
                for (IpMaintainServiceVO imsVO : uniqueMap.values()) {
                    // Step 2. 檢核 Group_ID + IP_ADDR 是否已存在，是的話做更新；否則新增
                    groupId = imsVO.getGroupId();
                    ipAddr = imsVO.getModifyIpAddr();
                    entity1 = ipMaintainDAO.findModuleIpDataSettingByUk(groupId, ipAddr);

                    if (entity1 != null) {
                        // 存在 => 更新
                        entity1.setMacAddr(imsVO.getModifyMacAddr());
                        entity1.setIpDesc(imsVO.getModifyIpDesc());
                        entity1.setUpdateTime(currentTimestamp());
                        entity1.setUpdateBy(currentUserName());
                        update1Entities.add(entity1);

                    } else {
                        // 不存在 => 新增
                        entity1 = new ModuleIpDataSetting();
                        entity1.setGroupId(imsVO.getGroupId());
                        entity1.setIpAddr(imsVO.getModifyIpAddr());
                        entity1.setMacAddr(imsVO.getModifyMacAddr());
                        entity1.setIpDesc(imsVO.getModifyIpDesc());
                        entity1.setCreateTime(currentTimestamp());
                        entity1.setCreateBy(currentUserName());
                        entity1.setUpdateTime(currentTimestamp());
                        entity1.setUpdateBy(currentUserName());
                        add1Entities.add(entity1);
                    }

                    if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
                    	// Step 2-2. 檢核 Group_ID + IP_ADDR 是否已存在Secondary DB，是的話做更新；否則新增
                        entity2 = ipMaintainDAO.findModuleIpDataSettingByUkFromSecondaryDB(groupId, ipAddr);

                        if (entity2 != null) {
                            // 存在 => 更新
                            entity2.setMacAddr(imsVO.getModifyMacAddr());
                            entity2.setIpDesc(imsVO.getModifyIpDesc());
                            entity2.setUpdateTime(currentTimestamp());
                            entity2.setUpdateBy(currentUserName());
                            update2Entities.add(entity2);

                        } else {
                            // 不存在 => 新增
                            entity2 = new ModuleIpDataSetting();
                            entity2.setGroupId(imsVO.getGroupId());
                            entity2.setIpAddr(imsVO.getModifyIpAddr());
                            entity2.setMacAddr(imsVO.getModifyMacAddr());
                            entity2.setIpDesc(imsVO.getModifyIpDesc());
                            entity2.setCreateTime(currentTimestamp());
                            entity2.setCreateBy(currentUserName());
                            entity2.setUpdateTime(currentTimestamp());
                            entity2.setUpdateBy(currentUserName());
                            add2Entities.add(entity2);
                        }
                    }
                    
                }

                if (add1Entities != null && !add1Entities.isEmpty()) {
                    ipMaintainDAO.insertEntities(BaseDAO.TARGET_PRIMARY_DB, add1Entities);
                }
                if (update1Entities != null && !update1Entities.isEmpty()) {
                    ipMaintainDAO.updateEntities(BaseDAO.TARGET_PRIMARY_DB, update1Entities);
                }
                if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
                	if (add2Entities != null && !add2Entities.isEmpty()) {
                        ipMaintainDAO.insertEntities(BaseDAO.TARGET_SECONDARY_DB, add2Entities);
                    }
                    if (update2Entities != null && !update2Entities.isEmpty()) {
                        ipMaintainDAO.updateEntities(BaseDAO.TARGET_SECONDARY_DB, update2Entities);
                    }
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
            List<ModuleIpDataSetting> update1Entities = new ArrayList<>();
            List<ModuleIpDataSetting> update2Entities = new ArrayList<>();

            ModuleIpDataSetting entity1;
            ModuleIpDataSetting entity2;
            String groupId;
            String ipAddr;
            String modifyMacAddr;
            String modifyIpDesc;
            for (IpMaintainServiceVO imsVO : updateList) {
                groupId = imsVO.getGroupId();
                ipAddr = imsVO.getIpAddr();

                // Step 1. 查找 Primary DB 資料
                entity1 = ipMaintainDAO.findModuleIpDataSettingByUk(groupId, ipAddr);

                if (entity1 == null) {
                    continue;

                } else {
                    modifyMacAddr = imsVO.getModifyMacAddr();
                    modifyIpDesc = imsVO.getModifyIpDesc();

                    entity1.setMacAddr(modifyMacAddr);
                    entity1.setIpDesc(modifyIpDesc);
                    entity1.setUpdateTime(currentTimestamp());
                    entity1.setUpdateBy(currentUserName());
                    update1Entities.add(entity1);
                }

                if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
                	// Step 2. 查找 Secondary DB 資料
                    entity2 = ipMaintainDAO.findModuleIpDataSettingByUkFromSecondaryDB(groupId, ipAddr);

                    if (entity2 == null) {
                        continue;

                    } else {
                        entity2.setMacAddr(modifyMacAddr);
                        entity2.setIpDesc(modifyIpDesc);
                        entity2.setUpdateTime(currentTimestamp());
                        entity2.setUpdateBy(currentUserName());
                        update2Entities.add(entity2);
                    }
                }
            }

            if (update1Entities != null && !update1Entities.isEmpty()) {
                ipMaintainDAO.updateEntities(BaseDAO.TARGET_PRIMARY_DB, update1Entities);
            }
            if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
            	if (update2Entities != null && !update2Entities.isEmpty()) {
                    ipMaintainDAO.updateEntities(BaseDAO.TARGET_SECONDARY_DB, update2Entities);
                }
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
            List<ModuleIpDataSetting> delete1Entities = new ArrayList<>();
            List<ModuleIpDataSetting> delete2Entities = new ArrayList<>();

            ModuleIpDataSetting entity1;
            ModuleIpDataSetting entity2;
            String groupId;
            String ipAddr;
            for (IpMaintainServiceVO imsVO : deleteList) {
                groupId = imsVO.getGroupId();
                ipAddr = imsVO.getIpAddr();

                // Step 1. 查找 Primary DB 資料
                entity1 = ipMaintainDAO.findModuleIpDataSettingByUk(groupId, ipAddr);

                if (entity1 == null) {
                    continue;

                } else {
                    delete1Entities.add(entity1);
                }

                if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
                	// Step 2. 查找 Secondary DB 資料
                    entity2 = ipMaintainDAO.findModuleIpDataSettingByUkFromSecondaryDB(groupId, ipAddr);

                    if (entity2 == null) {
                        continue;

                    } else {
                        delete2Entities.add(entity2);
                    }
                }
            }

            if (delete1Entities != null && !delete1Entities.isEmpty()) {
                ipMaintainDAO.deleteEntities(BaseDAO.TARGET_PRIMARY_DB, delete1Entities);
            }
            if (Constants.DATA_Y.equalsIgnoreCase(Env.ENABLE_SECONDARY_DB)) {
            	if (delete2Entities != null && !delete2Entities.isEmpty()) {
                    ipMaintainDAO.deleteEntities(BaseDAO.TARGET_SECONDARY_DB, delete2Entities);
                }
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("刪除失敗");
        }
        return retVO;
    }
}
