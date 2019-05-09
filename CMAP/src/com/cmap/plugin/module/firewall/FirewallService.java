package com.cmap.plugin.module.firewall;

import java.util.List;
import com.cmap.exception.ServiceLayerException;

public interface FirewallService {

    public List<String> getFieldNameList(String queryType, String fieldType);

    public List<FirewallVO> findFirewallLogSetting(String settingName);

    public long countFirewallLogRecordFromDB(FirewallVO fVO, List<String> searchLikeField) throws ServiceLayerException;

    public List<FirewallVO> findFirewallLogRecordFromDB(
            FirewallVO fVO, Integer startRow, Integer pageLength, List<String> searchLikeField) throws ServiceLayerException;

}
