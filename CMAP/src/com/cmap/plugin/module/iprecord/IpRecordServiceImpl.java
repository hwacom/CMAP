package com.cmap.plugin.module.iprecord;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.annotation.Log;
import com.cmap.exception.ServiceLayerException;
import com.cmap.service.impl.CommonServiceImpl;

@Service("ipRecordService")
@Transactional
public class IpRecordServiceImpl extends CommonServiceImpl implements IpRecordService {
    @Log
    private static Logger log;

    @Autowired
    private IpRecordDAO ipRecordDAO;


    @Override
    public long countModuleBlockedIpList(IpRecordVO irVO) throws ServiceLayerException {
        long retVal = 0;
        try {
            retVal = ipRecordDAO.countModuleBlockedIpList(irVO);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("查詢失敗!");
        }
        return retVal;
    }

    @Override
    public List<IpRecordVO> findModuleBlockedIpList(IpRecordVO irVO, Integer startRow, Integer pageLength)
            throws ServiceLayerException {
        List<IpRecordVO> retList = new ArrayList<>();
        try {
            List<ModuleBlockedIpList> entities = ipRecordDAO.findModuleBlockedIpList(irVO, startRow, pageLength);

            if (entities == null || (entities != null && entities.isEmpty())) {
                throw new ServiceLayerException("查無資料!");
            }

            IpRecordVO vo;
            for (ModuleBlockedIpList entity : entities) {
                vo = new IpRecordVO();
                BeanUtils.copyProperties(entity, vo);
                vo.setBlockTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getBlockTime()));
                vo.setOpenTimeStr(entity.getOpenTime() != null ? Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getOpenTime()) : null);
                vo.setUpdateTimeStr(Constants.FORMAT_YYYYMMDD_HH24MISS.format(entity.getUpdateTime()));

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
