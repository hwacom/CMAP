package com.cmap.plugin.module.ip.maintain;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
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
                DeviceList dlEntity;
                IpMaintainServiceVO vo;

                for (Object[] obj : entities) {
                    midsEntity = (ModuleIpDataSetting)obj[0];
                    dlEntity = (DeviceList)obj[1];

                    vo = new IpMaintainServiceVO();
                    BeanUtils.copyProperties(midsEntity, vo);
                    vo.setGroupName(dlEntity.getGroupName());

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
            List<ModuleIpDataSetting> addEntities = new ArrayList();
            ModuleIpDataSetting entity;

            for (IpMaintainServiceVO imsVO : addList) {
                entity = new ModuleIpDataSetting();
                entity.setGroupId(imsVO.getGroupId());
                entity.setIpAddr(imsVO.getIpAddr());
                entity.setIpDesc(imsVO.getIpDesc());
                entity.setCreateTime(currentTimestamp());
                entity.setCreateBy(currentUserName());
                entity.setUpdateTime(currentTimestamp());
                entity.setUpdateBy(currentUserName());
                addEntities.add(entity);
            }
            ipMaintainDAO.insertEntities(addEntities);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }

    @Override
    public IpMaintainServiceVO updateIpDataSetting(List<IpMaintainServiceVO> updateList)
            throws ServiceLayerException {
        IpMaintainServiceVO retVO = new IpMaintainServiceVO();
        try {
            List<ModuleIpDataSetting> updateEntities = new ArrayList();
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
                ipMaintainDAO.updateEntities(updateEntities);
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
            List<ModuleIpDataSetting> deleteEntities = new ArrayList();
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
                ipMaintainDAO.deleteEntities(deleteEntities);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("刪除失敗");
        }
        return retVO;
    }
}
