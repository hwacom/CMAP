package com.cmap.plugin.module.ip.mapping;

import java.util.List;
import com.cmap.dao.BaseDAO;

public interface IpMappingDAO extends BaseDAO {

    public List<ModuleMacTableExcludePort> findModuleMacTableExcludePort(String groupId, String deviceId);

    public List<Object[]> findEachIpAddressLastestModuleIpMacPortMapping(String groupId);

    public long countModuleIpMacPortMappingChange(IpMappingServiceVO imsVO);

    public List<Object[]> findModuleIpMacPortMappingChange(IpMappingServiceVO imsVO,
	        Integer startRow, Integer pageLength);

    public List<Object[]> findNearlyModuleIpMacPortMappingByTime(String groupId, String ipAddress, String date, String time);
}
