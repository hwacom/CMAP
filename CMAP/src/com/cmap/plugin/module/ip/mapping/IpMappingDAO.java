package com.cmap.plugin.module.ip.mapping;

import java.util.List;
import com.cmap.model.MibOidMapping;

public interface IpMappingDAO {

    public List<ModuleMacTableExcludePort> findModuleMacTableExcludePort(String groupId, String deviceId);

    public List<MibOidMapping> findMibOidMappingByNames(List<String> oidNames);
}
