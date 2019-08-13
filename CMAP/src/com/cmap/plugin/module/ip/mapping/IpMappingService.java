package com.cmap.plugin.module.ip.mapping;

import java.util.Date;
import java.util.List;
import com.cmap.exception.ServiceLayerException;

public interface IpMappingService {

    public IpMappingServiceVO executeIpMappingPolling(String groupId, Date executeDate) throws ServiceLayerException;

    public long countModuleIpMacPortMappingChange(IpMappingServiceVO imsVO) throws ServiceLayerException;

    public List<IpMappingServiceVO> findModuleIpMacPortMappingChange(IpMappingServiceVO imsVO) throws ServiceLayerException;
}
