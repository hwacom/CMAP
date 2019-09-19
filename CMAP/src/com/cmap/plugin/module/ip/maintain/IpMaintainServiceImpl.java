package com.cmap.plugin.module.ip.maintain;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cmap.annotation.Log;
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
            ipMaintainDAO.insertEntities(addList);

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
            ipMaintainDAO.updateEntities(updateList);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }

    @Override
    public IpMaintainServiceVO deleteIpDataSetting(List<IpMaintainServiceVO> deleteList)
            throws ServiceLayerException {
        IpMaintainServiceVO retVO = new IpMaintainServiceVO();
        try {
            ipMaintainDAO.deleteEntities(deleteList);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
        return retVO;
    }
}
