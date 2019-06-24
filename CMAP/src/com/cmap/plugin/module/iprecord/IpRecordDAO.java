package com.cmap.plugin.module.iprecord;

import java.util.List;

public interface IpRecordDAO {

    public long countModuleBlockedIpList(IpRecordVO irVO);

    public List<ModuleBlockedIpList> findModuleBlockedIpList(IpRecordVO irVO, Integer startRow, Integer pageLength);
}
