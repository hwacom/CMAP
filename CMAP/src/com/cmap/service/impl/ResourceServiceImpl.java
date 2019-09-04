package com.cmap.service.impl;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cmap.Constants;
import com.cmap.Env;
import com.cmap.annotation.Log;
import com.cmap.dao.ResourceDAO;
import com.cmap.exception.ServiceLayerException;
import com.cmap.model.ResourceInfo;
import com.cmap.service.ResourceService;
import com.cmap.service.vo.ResourceServiceVO;

@Service("resourceService")
@Transactional
public class ResourceServiceImpl extends CommonServiceImpl implements ResourceService {
    @Log
    private static Logger log;

    @Autowired
    private ResourceDAO resourceDAO;

    @Override
    public ResourceServiceVO getResourceInfo(String id) throws ServiceLayerException {
        ResourceServiceVO retVO = null;
        try {
            ResourceInfo ri = resourceDAO.getResourceInfoById(id);

            if (ri == null) {
                throw new ServiceLayerException("無效連結");

            } else {
                String statusFlag = ri.getStatusFlag();

                if (StringUtils.equals(statusFlag, Constants.STATUS_FLAG_DELETE)) {
                    throw new ServiceLayerException("連結已失效，請重新產生");
                }

                retVO = new ResourceServiceVO();
                BeanUtils.copyProperties(retVO, ri);

                String fileFullName = ri.getFileFullName();
                Path path = FileSystems.getDefault().getPath(Env.DEFAULT_DATA_EXPORT_TEMP_LOCATION, fileFullName);
                InputStream is = Files.newInputStream(path);

                retVO.setInputStream(is);
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("下載失敗");
        }
        return retVO;
    }

    @Override
    public String addResourceInfo(ResourceServiceVO rsVO) throws ServiceLayerException {
        String retId = null;
        try {
            ResourceInfo newEntity = new ResourceInfo();
            BeanUtils.copyProperties(newEntity, rsVO);
            newEntity.setDownloadTimes(0);
            newEntity.setStatusFlag(Constants.STATUS_FLAG_OPEN);
            newEntity.setCreateTime(currentTimestamp());
            newEntity.setCreateBy(currentUserName());
            newEntity.setUpdateTime(currentTimestamp());
            newEntity.setUpdateBy(currentUserName());

            retId = (String)resourceDAO.insertEntityAndGetReturnIdValue(newEntity);

        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ServiceLayerException("資料寫入失敗，請重新操作");
        }

        return retId;
    }
}
