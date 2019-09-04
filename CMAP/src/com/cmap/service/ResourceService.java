package com.cmap.service;

import com.cmap.exception.ServiceLayerException;
import com.cmap.service.vo.ResourceServiceVO;

public interface ResourceService {

    public ResourceServiceVO getResourceInfo(String id) throws ServiceLayerException;

    public String addResourceInfo(ResourceServiceVO rsVO) throws ServiceLayerException;
}
