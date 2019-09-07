package com.cmap.plugin.module.port.blocked.record;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.DeviceList;
import com.cmap.model.DevicePortInfo;
import com.cmap.service.impl.CommonServiceImpl;

@Service("portRecordService")
@Transactional
public class PortBlockedRecordServiceImpl extends CommonServiceImpl implements PortBlockedRecordService {
    @Log
    private static Logger log;

    @Autowired
    private PortBlockedRecordDAO portRecordDAO;

    @Override
    public long countModuleBlockedPortList(PortBlockedRecordVO irVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = portRecordDAO.countModuleBlockedPortList(irVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<PortBlockedRecordVO> findModuleBlockedPortList(PortBlockedRecordVO irVO,
            Integer startRow, Integer pageLength) throws ServiceLayerException {
        List<PortBlockedRecordVO> retList = new ArrayList<>();
        try {
            List<Object[]> entities = portRecordDAO.findModuleBlockedPortList(irVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                throw new ServiceLayerException("查無資料!");
            }

            ModuleBlockedPortList bilEntity;
            DeviceList dlEntity;
            DevicePortInfo dpiEntity;
            PortBlockedRecordVO vo;
            for (Object[] entity : entities) {
                bilEntity = (ModuleBlockedPortList)entity[0];
                dlEntity = (DeviceList)entity[1];
                dpiEntity = (DevicePortInfo)entity[2];
                vo = new PortBlockedRecordVO();

                BeanUtils.copyProperties(bilEntity, vo);
                vo.setBlockTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getBlockTime()));
                vo.setOpenTimeStr(bilEntity.getOpenTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getOpenTime()) : null);
                vo.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(bilEntity.getUpdateTime()));
                vo.setGroupName(dlEntity.getGroupName());
                vo.setDeviceName(dlEntity.getDeviceName());
                vo.setPortName(dpiEntity.getPortName());
                vo.setStatusFlag(StringUtils.equals(bilEntity.getStatusFlag(), "B")
                                    ? "B-封鎖"
                                    : StringUtils.equals(bilEntity.getStatusFlag(), "O") ? "O-開通" : "N/A");

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

}
